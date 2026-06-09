package com.dsa.interview;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.stream.Collectors;

/**
 * =====================================================
 * SYSTEM DESIGN: RIDE-HAILING SERVICE (Uber-like)
 * =====================================================
 *
 * =====================================================
 * HIGH-LEVEL DESIGN (HLD)
 * =====================================================
 *
 * --- REQUIREMENTS ---
 * Functional:
 * 1. Riders can request rides with pickup/dropoff locations
 * 2. Drivers can go online/offline and receive ride requests
 * 3. Real-time driver location tracking (GPS every 3-5 seconds)
 * 4. Match rider with nearest available driver
 * 5. ETA calculation and fare estimation
 * 6. Ride lifecycle: REQUESTED → ACCEPTED → ARRIVED → IN_PROGRESS → COMPLETED
 * 7. Surge pricing during high demand
 * 8. Payment processing
 * 9. Ride history and ratings
 *
 * Non-Functional:
 * - 100M+ users, 10M+ rides/day
 * - < 1s matching time
 * - 99.99% availability
 * - Real-time location updates (WebSocket)
 * - Handle flash crowds (concerts, sports events)
 *
 * --- SYSTEM ARCHITECTURE ---
 *
 * ┌──────────────┐              ┌──────────────┐
 * │ Rider App    │              │ Driver App   │
 * │ (WebSocket)  │              │ (WebSocket)  │
 * └──────┬───────┘              └──────┬───────┘
 *        │                              │
 * ┌──────▼──────────────────────────────▼───────┐
 * │              API Gateway / Load Balancer     │
 * └──────┬──────────────────────────────┬───────┘
 *        │                              │
 * ┌──────▼──────┐              ┌───────▼───────┐
 * │ Rider       │              │ Driver        │
 * │ Service     │              │ Service       │
 * └──────┬──────┘              └───────┬───────┘
 *        │                              │
 * ┌──────▼──────────────────────────────▼───────┐
 * │              Dispatch Service                │
 * │      (Matching Engine + QuadTree Index)      │
 * └──────┬──────────────────────────────┬───────┘
 *        │                              │
 * ┌──────▼──────┐              ┌───────▼───────┐
 * │  Location   │              │  Trip         │
 * │  Service    │              │  Service      │
 * │  (Redis)    │              │               │
 * └──────┬──────┘              └───────┬───────┘
 *        │                              │
 * ┌──────▼──────┐              ┌───────▼───────┐
 * │  Map/ETA    │              │  Pricing      │
 * │  Service    │              │  Service      │
 * └─────────────┘              └───────────────┘
 *
 * --- DATA FLOW: RIDE REQUEST ---
 *
 * 1. Rider requests ride via app (pickup, dropoff coordinates)
 * 2. Rider Service validates rider account and payment method
 * 3. Dispatch Service queries Location Service for nearby drivers
 *    - Redis GEORADIUS pickup_lat pickup_lng radius_km
 * 4. Filter available drivers (online, not on trip, in correct zone)
 * 5. Calculate ETA for top 10 candidates
 * 6. Determine pricing (base + distance + time + surge multiplier)
 * 7. Send ride request to top N drivers (usually 3-5)
 * 8. First driver to accept gets the ride
 * 9. Other drivers get "ride no longer available" notification
 * 10. Rider and Driver receive match confirmation
 *
 * --- LOCATION INDEXING ---
 *
 * Option 1: Redis Geospatial (GEOADD, GEORADIUS)
 *   - Simple, fast, widely used
 *   - GEOADD drivers:driver_id longitude latitude
 *   - GEORADIUS pickup_lat pickup_lng 5 km
 *
 * Option 2: QuadTree (for finer control)
 *   - Divide geographic area into quadrants
 *   - Each node covers a region
 *   - Insert drivers into leaf nodes based on location
 *   - Search nearby by traversing tree
 *
 * Option 3: Grid-based (Uber's approach - H3 Hexagonal)
 *   - Uber H3: hexagonal hierarchical spatial index
 *   - Each hexagon has unique ID
 *   - Drivers bucketed by hex ID
 *   - Search neighboring hexagons
 *
 * --- DATABASE SCHEMA ---
 *
 * users (PostgreSQL):
 *   id (UUID PK), name, phone, email, payment_method, created_at
 *
 * drivers (PostgreSQL):
 *   id (UUID PK), user_id (FK), vehicle_type, license_plate,
 *   status (ONLINE/OFFLINE/ON_TRIP), rating, total_trips
 *
 * trips (Cassandra - write-optimized):
 *   trip_id (UUID PK)
 *   rider_id (UUID)
 *   driver_id (UUID)
 *   pickup_lat, pickup_lng (DOUBLE)
 *   dropoff_lat, dropoff_lng (DOUBLE)
 *   status (TEXT)
 *   fare (DOUBLE)
 *   surge_multiplier (DOUBLE)
 *   distance_km (DOUBLE)
 *   duration_min (INT)
 *   created_at (TIMESTAMP)
 *   accepted_at (TIMESTAMP)
 *   started_at (TIMESTAMP)
 *   completed_at (TIMESTAMP)
 *   PRIMARY KEY ((rider_id), created_at)
 *
 * locations (Redis):
 *   driver_id → {lat, lng, updated_at}
 *   Geospatial index for radius queries
 *
 * =====================================================
 * LOW-LEVEL DESIGN (LLD) - Java Implementation
 * =====================================================
 */
public class DesignRideHailingService {

    // =====================================================
    // 1. CORE DATA MODELS
    // =====================================================

    public enum RideStatus {
        REQUESTED, ACCEPTED, DRIVER_ARRIVED, IN_PROGRESS, COMPLETED, CANCELLED
    }

    public enum DriverStatus {
        ONLINE, OFFLINE, ON_TRIP
    }

    public enum VehicleType {
        ECONOMY, COMFORT, PREMIUM, XL, AUTO
    }

    /**
     * Location coordinates (latitude, longitude).
     */
    public record Location(double latitude, double longitude) {
        /**
         * Haversine distance calculator (in km).
         */
        public double distanceTo(Location other) {
            final double EARTH_RADIUS_KM = 6371.0;
            double lat1 = Math.toRadians(this.latitude);
            double lon1 = Math.toRadians(this.longitude);
            double lat2 = Math.toRadians(other.latitude);
            double lon2 = Math.toRadians(other.longitude);

            double dlat = lat2 - lat1;
            double dlon = lon2 - lon1;

            double a = Math.sin(dlat / 2) * Math.sin(dlat / 2)
                    + Math.cos(lat1) * Math.cos(lat2)
                    * Math.sin(dlon / 2) * Math.sin(dlon / 2);
            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

            return EARTH_RADIUS_KM * c;
        }
    }

    /**
     * Driver model.
     */
    public static class Driver {
        private final String id;
        private final String name;
        private final VehicleType vehicleType;
        private volatile DriverStatus status;
        private volatile Location currentLocation;
        private volatile double rating;
        private int totalTrips;
        private volatile String currentTripId;
        private final long registeredAt;

        public Driver(String id, String name, VehicleType vehicleType) {
            this.id = id;
            this.name = name;
            this.vehicleType = vehicleType;
            this.status = DriverStatus.OFFLINE;
            this.rating = 5.0;
            this.totalTrips = 0;
            this.registeredAt = System.currentTimeMillis();
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public VehicleType getVehicleType() { return vehicleType; }
        public DriverStatus getStatus() { return status; }
        public void setStatus(DriverStatus status) { this.status = status; }
        public Location getCurrentLocation() { return currentLocation; }
        public void setCurrentLocation(Location location) { this.currentLocation = location; }
        public double getRating() { return rating; }
        public void setRating(double rating) { this.rating = rating; }
        public String getCurrentTripId() { return currentTripId; }
        public void setCurrentTripId(String tripId) { this.currentTripId = tripId; }

        public boolean isAvailable() {
            return status == DriverStatus.ONLINE && currentTripId == null;
        }
    }

    /**
     * Rider model.
     */
    public static class Rider {
        private final String id;
        private final String name;
        private final String phone;
        private final String paymentMethod;

        public Rider(String id, String name, String phone, String paymentMethod) {
            this.id = id;
            this.name = name;
            this.phone = phone;
            this.paymentMethod = paymentMethod;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getPaymentMethod() { return paymentMethod; }
    }

    /**
     * Ride/Trip model - the core entity.
     */
    public static class Trip {
        private final String id;
        private final String riderId;
        private volatile String driverId;
        private final Location pickupLocation;
        private final Location dropoffLocation;
        private volatile RideStatus status;
        private volatile double fare;
        private double surgeMultiplier;
        private double distanceKm;
        private volatile int durationMin;
        private final long createdAt;
        private volatile long acceptedAt;
        private volatile long startedAt;
        private volatile long completedAt;

        public Trip(String id, String riderId, Location pickup, Location dropoff) {
            this.id = id;
            this.riderId = riderId;
            this.pickupLocation = pickup;
            this.dropoffLocation = dropoff;
            this.status = RideStatus.REQUESTED;
            this.fare = 0;
            this.surgeMultiplier = 1.0;
            this.createdAt = System.currentTimeMillis();
        }

        // Status transitions
        public void accept(String driverId) {
            this.driverId = driverId;
            this.status = RideStatus.ACCEPTED;
            this.acceptedAt = System.currentTimeMillis();
        }

        public void driverArrived() {
            this.status = RideStatus.DRIVER_ARRIVED;
        }

        public void start() {
            this.status = RideStatus.IN_PROGRESS;
            this.startedAt = System.currentTimeMillis();
        }

        public void complete() {
            this.status = RideStatus.COMPLETED;
            this.completedAt = System.currentTimeMillis();
            calculateFare();
        }

        public void cancel() {
            this.status = RideStatus.CANCELLED;
        }

        /**
         * Calculate fare based on distance, time, surge, vehicle type.
         */
        private void calculateFare() {
            // Example: ₹10 base + ₹8/km + ₹2/min + surge
            double baseFare = 10.0;
            double perKm = 8.0;
            double perMin = 2.0;

            this.distanceKm = pickupLocation.distanceTo(dropoffLocation);
            long elapsedMs = completedAt - startedAt;
            this.durationMin = (int) (elapsedMs / 60000);

            this.fare = (baseFare + distanceKm * perKm + durationMin * perMin) * surgeMultiplier;
        }

        // Getters
        public String getId() { return id; }
        public String getRiderId() { return riderId; }
        public String getDriverId() { return driverId; }
        public Location getPickupLocation() { return pickupLocation; }
        public Location getDropoffLocation() { return dropoffLocation; }
        public RideStatus getStatus() { return status; }
        public double getFare() { return fare; }
        public double getSurgeMultiplier() { return surgeMultiplier; }
        public void setSurgeMultiplier(double m) { this.surgeMultiplier = m; }
        public double getDistanceKm() { return distanceKm; }
        public int getDurationMin() { return durationMin; }
        public long getCreatedAt() { return createdAt; }
    }

    // =====================================================
    // 2. LOCATION SERVICE (Redis-backed geospatial)
    // =====================================================

    /**
     * LocationService - tracks driver locations in real-time.
     * In production: backed by Redis Geospatial (GEOADD, GEORADIUS).
     */
    public static class LocationService {
        // driver_id → Location
        private final ConcurrentHashMap<String, Location> driverLocations = new ConcurrentHashMap<>();
        // Simple spatial index: grid cells → set of driver IDs
        private final ConcurrentHashMap<String, Set<String>> gridIndex = new ConcurrentHashMap<>();
        // Grid cell size in degrees (~1km at equator)
        private static final double GRID_CELL_SIZE = 0.01;

        /**
         * Update driver's location.
         * Called every 3-5 seconds from driver's app via WebSocket.
         */
        public void updateLocation(String driverId, Location location) {
            // Remove from old grid cell
            Location oldLocation = driverLocations.get(driverId);
            if (oldLocation != null) {
                String oldCell = getGridCell(oldLocation);
                Set<String> driversInCell = gridIndex.get(oldCell);
                if (driversInCell != null) {
                    driversInCell.remove(driverId);
                }
            }

            // Update location
            driverLocations.put(driverId, location);

            // Add to new grid cell
            String newCell = getGridCell(location);
            gridIndex.computeIfAbsent(newCell, k -> ConcurrentHashMap.newKeySet()).add(driverId);
        }

        /**
         * Get driver's current location.
         */
        public Location getLocation(String driverId) {
            return driverLocations.get(driverId);
        }

        /**
         * Find nearby drivers within radius (km).
         * Uses grid-based approach O(1) for small radii.
         */
        public List<String> findNearbyDrivers(Location center, double radiusKm, int maxResults) {
            Set<String> candidates = new HashSet<>();

            // Convert radius to approximate grid cells to scan
            double radiusDeg = radiusKm / 111.0; // 1° ≈ 111km
            int cellsToScan = Math.max(1, (int) (radiusDeg / GRID_CELL_SIZE));

            String centerCell = getGridCell(center);
            String[] centerParts = centerCell.split(",");
            int centerRow = Integer.parseInt(centerParts[0]);
            int centerCol = Integer.parseInt(centerParts[1]);

            // Scan neighboring cells
            for (int dr = -cellsToScan; dr <= cellsToScan; dr++) {
                for (int dc = -cellsToScan; dc <= cellsToScan; dc++) {
                    String cellKey = (centerRow + dr) + "," + (centerCol + dc);
                    Set<String> driversInCell = gridIndex.get(cellKey);
                    if (driversInCell != null) {
                        candidates.addAll(driversInCell);
                    }
                }
            }

            // Filter by actual distance and limit
            List<ScoredDriver> scored = new ArrayList<>();
            for (String driverId : candidates) {
                Location loc = driverLocations.get(driverId);
                if (loc != null) {
                    double distance = center.distanceTo(loc);
                    if (distance <= radiusKm) {
                        scored.add(new ScoredDriver(driverId, distance));
                    }
                }
            }

            // Sort by distance, return top N
            scored.sort(Comparator.comparingDouble(ScoredDriver::distance));
            return scored.stream()
                    .limit(maxResults)
                    .map(ScoredDriver::driverId)
                    .collect(Collectors.toList());
        }

        /**
         * Remove driver from tracking (when going offline).
         */
        public void removeDriver(String driverId) {
            Location loc = driverLocations.remove(driverId);
            if (loc != null) {
                String cell = getGridCell(loc);
                Set<String> driversInCell = gridIndex.get(cell);
                if (driversInCell != null) {
                    driversInCell.remove(driverId);
                }
            }
        }

        private String getGridCell(Location loc) {
            int row = (int) (loc.latitude() / GRID_CELL_SIZE);
            int col = (int) (loc.longitude() / GRID_CELL_SIZE);
            return row + "," + col;
        }

        private record ScoredDriver(String driverId, double distance) {}
    }

    // =====================================================
    // 3. PRICING SERVICE (Surge Pricing)
    // =====================================================

    /**
     * PricingService - calculates fares and surge pricing.
     *
     * Surge Pricing Logic:
     * - Track demand (ride requests) and supply (available drivers) per region
     * - surge_multiplier = max(1.0, demand / (supply * target_ratio))
     * - Increase multiplier when demand >> supply
     * - Smooth transitions: max change of 0.1x per interval
     */
    public static class PricingService {
        // Region → demand count (pending requests)
        private final ConcurrentHashMap<String, AtomicInteger> demandCounter = new ConcurrentHashMap<>();
        // Region → supply count (available drivers)
        private final ConcurrentHashMap<String, AtomicInteger> supplyCounter = new ConcurrentHashMap<>();
        // Region → current surge multiplier
        private final ConcurrentHashMap<String, AtomicDouble> surgeMultipliers = new ConcurrentHashMap<>();
        // Target ratio of supply to demand
        private static final double TARGET_SUPPLY_DEMAND_RATIO = 1.5;

        static class AtomicDouble {
            private final AtomicReference<Double> value = new AtomicReference<>(1.0);
            public double get() { return value.get(); }
            public void set(double newVal) { value.set(newVal); }
        }

        /**
         * Calculate surge multiplier for a region.
         */
        public double getSurgeMultiplier(String regionId) {
            return surgeMultipliers.computeIfAbsent(regionId, k -> new AtomicDouble()).get();
        }

        /**
         * Update supply/demand and recalculate surge.
         */
        public void updateDemand(String regionId) {
            demandCounter.computeIfAbsent(regionId, k -> new AtomicInteger()).incrementAndGet();
            recalculateSurge(regionId);
        }

        public void updateSupply(String regionId, int count) {
            supplyCounter.computeIfAbsent(regionId, k -> new AtomicInteger()).set(count);
            recalculateSurge(regionId);
        }

        private void recalculateSurge(String regionId) {
            int demand = demandCounter.getOrDefault(regionId, new AtomicInteger()).get();
            int supply = supplyCounter.getOrDefault(regionId, new AtomicInteger()).get();

            if (supply == 0) {
                setSurge(regionId, 3.0); // Max surge when no drivers available
                return;
            }

            double ratio = (double) demand / supply;
            double targetRatio = TARGET_SUPPLY_DEMAND_RATIO;

            // Calculate desired multiplier
            double desiredMultiplier = Math.max(1.0, ratio / targetRatio);
            desiredMultiplier = Math.min(3.0, desiredMultiplier); // Cap at 3x

            // Smooth transition: max change of 0.2x
            AtomicDouble current = surgeMultipliers.get(regionId);
            if (current != null) {
                double currentVal = current.get();
                double diff = desiredMultiplier - currentVal;
                double step = Math.max(-0.2, Math.min(0.2, diff));
                setSurge(regionId, currentVal + step);
            } else {
                setSurge(regionId, desiredMultiplier);
            }
        }

        private void setSurge(String regionId, double multiplier) {
            surgeMultipliers.computeIfAbsent(regionId, k -> new AtomicDouble()).set(multiplier);
        }

        /**
         * Decay demand over time (requests expire).
         */
        public void decayDemand(String regionId) {
            AtomicInteger counter = demandCounter.get(regionId);
            if (counter != null && counter.get() > 0) {
                counter.decrementAndGet();
            }
        }

        /**
         * Estimate fare for a trip.
         */
        public double estimateFare(Location pickup, Location dropoff,
                                   VehicleType vehicleType, double surgeMultiplier) {
            double distance = pickup.distanceTo(dropoff);

            Map<VehicleType, double[]> rates = new HashMap<>();
            rates.put(VehicleType.ECONOMY, new double[]{15, 6, 1.5});   // base, per km, per min
            rates.put(VehicleType.COMFORT, new double[]{25, 10, 2.5});
            rates.put(VehicleType.PREMIUM, new double[]{50, 18, 4.0});
            rates.put(VehicleType.XL, new double[]{35, 14, 3.0});
            rates.put(VehicleType.AUTO, new double[]{8, 4, 1.0});

            double[] rate = rates.getOrDefault(vehicleType, rates.get(VehicleType.ECONOMY));
            double estimatedMinutes = distance / 30 * 60; // Assume avg 30 km/h

            double fare = (rate[0] + distance * rate[1] + estimatedMinutes * rate[2]) * surgeMultiplier;
            return Math.round(fare * 100.0) / 100.0;
        }
    }

    // =====================================================
    // 4. DISPATCH SERVICE (Matching Engine)
    // =====================================================

    /**
     * DispatchService - the core matching engine.
     * Finds the best driver for a ride request.
     */
    public static class DispatchService {
        private final LocationService locationService;
        private final PricingService pricingService;
        private final ConcurrentHashMap<String, Driver> drivers;
        private final ConcurrentHashMap<String, Trip> activeTrips;
        // Pending ride requests: rideId → Trip
        private final ConcurrentHashMap<String, Trip> pendingRequests = new ConcurrentHashMap<>();
        // Driver ride requests: driverId → Set of offered trip IDs
        private final ConcurrentHashMap<String, Set<String>> driverOffers = new ConcurrentHashMap<>();
        // Max drivers to notify per request
        private static final int MAX_DRIVERS_TO_OFFER = 5;
        // Offer timeout (driver has 15 seconds to accept)
        private static final long OFFER_TIMEOUT_MS = 15_000;
        // Search radius (starts at 3km, expands)
        private static final double INITIAL_SEARCH_RADIUS_KM = 3.0;
        private static final double MAX_SEARCH_RADIUS_KM = 15.0;

        private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        public DispatchService(LocationService locationService, PricingService pricingService,
                               ConcurrentHashMap<String, Driver> drivers,
                               ConcurrentHashMap<String, Trip> activeTrips) {
            this.locationService = locationService;
            this.pricingService = pricingService;
            this.drivers = drivers;
            this.activeTrips = activeTrips;
        }

        /**
         * Find and offer ride to nearby drivers.
         * Returns the trip if matched.
         */
        public CompletableFuture<Trip> requestRide(Rider rider, Location pickup,
                                                    Location dropoff, VehicleType vehicleType) {
            CompletableFuture<Trip> future = new CompletableFuture<>();

            // Create trip
            String tripId = "trip_" + UUID.randomUUID().toString().substring(0, 8);
            Trip trip = new Trip(tripId, rider.getId(), pickup, dropoff);
            pendingRequests.put(tripId, trip);

            // Get region for surge pricing
            String region = getRegion(pickup);
            double surge = pricingService.getSurgeMultiplier(region);
            trip.setSurgeMultiplier(surge);

            double estimatedFare = pricingService.estimateFare(pickup, dropoff, vehicleType, surge);
            System.out.println("  [Dispatch] Ride requested: trip=" + tripId
                    + ", rider=" + rider.getId()
                    + ", pickup=" + pickup
                    + ", estimated fare=₹" + estimatedFare
                    + ", surge=" + surge + "x");

            // Find nearby drivers with expanding radius
            findDriversAndOffer(trip, vehicleType, INITIAL_SEARCH_RADIUS_KM, future);

            return future;
        }

        /**
         * Recursively find drivers with expanding radius.
         */
        private void findDriversAndOffer(Trip trip, VehicleType vehicleType,
                                          double radius, CompletableFuture<Trip> future) {
            List<String> nearbyDriverIds = locationService.findNearbyDrivers(
                    trip.getPickupLocation(), radius, MAX_DRIVERS_TO_OFFER * 3);

            // Filter available drivers matching vehicle type
            List<Driver> availableDrivers = new ArrayList<>();
            for (String driverId : nearbyDriverIds) {
                Driver driver = drivers.get(driverId);
                if (driver != null && driver.isAvailable()
                        && driver.getVehicleType() == vehicleType) {
                    availableDrivers.add(driver);
                }
            }

            if (!availableDrivers.isEmpty()) {
                // Sort by distance and rating
                availableDrivers.sort(Comparator
                        .<Driver, Double>comparing(d -> trip.getPickupLocation()
                                .distanceTo(d.getCurrentLocation()), Double::compare)
                        .thenComparingDouble(Driver::getRating));

                // Offer to top N drivers
                int driversToOffer = Math.min(MAX_DRIVERS_TO_OFFER, availableDrivers.size());
                for (int i = 0; i < driversToOffer; i++) {
                    offerTripToDriver(availableDrivers.get(i), trip, future);
                }
            } else if (radius < MAX_SEARCH_RADIUS_KM) {
                // Expand search radius
                double newRadius = Math.min(radius * 1.5, MAX_SEARCH_RADIUS_KM);
                System.out.println("  [Dispatch] No drivers found at " + radius
                        + "km, expanding to " + newRadius + "km");
                scheduler.schedule(() ->
                        findDriversAndOffer(trip, vehicleType, newRadius, future),
                        2, TimeUnit.SECONDS);
            } else {
                future.completeExceptionally(
                        new RuntimeException("No drivers available in your area"));
            }
        }

        /**
         * Offer a trip to a driver.
         */
        private void offerTripToDriver(Driver driver, Trip trip,
                                       CompletableFuture<Trip> future) {
            String offerKey = driver.getId() + ":" + trip.getId();

            driverOffers.computeIfAbsent(driver.getId(), k -> ConcurrentHashMap.newKeySet())
                    .add(trip.getId());

            System.out.println("  [Dispatch] Offered trip " + trip.getId()
                    + " to driver " + driver.getName()
                    + " (distance: "
                    + String.format("%.1f", trip.getPickupLocation()
                            .distanceTo(driver.getCurrentLocation()))
                    + " km)");

            // In production: send push notification via WebSocket
            // Driver has OFFER_TIMEOUT_MS to accept

            // Auto-expire offer after timeout
            scheduler.schedule(() -> {
                Set<String> offers = driverOffers.get(driver.getId());
                if (offers != null) {
                    offers.remove(trip.getId());
                }
                // Check if trip was accepted by someone else
                if (trip.getStatus() == RideStatus.REQUESTED) {
                    // Try next driver - handled by recursion
                }
            }, OFFER_TIMEOUT_MS, TimeUnit.MILLISECONDS);
        }

        /**
         * Driver accepts a ride offer.
         */
        public Trip acceptRide(String driverId, String tripId) {
            Trip trip = pendingRequests.get(tripId);
            if (trip == null || trip.getStatus() != RideStatus.REQUESTED) {
                return null; // Already taken or expired
            }

            Driver driver = drivers.get(driverId);
            if (driver == null || !driver.isAvailable()) {
                return null;
            }

            // Accept the ride
            synchronized (trip) {
                if (trip.getStatus() != RideStatus.REQUESTED) {
                    return null; // Someone else accepted first
                }
                trip.accept(driverId);
                driver.setCurrentTripId(tripId);
                driver.setStatus(DriverStatus.ON_TRIP);
                activeTrips.put(tripId, trip);
                pendingRequests.remove(tripId);
            }

            System.out.println("  [Dispatch] Driver " + driver.getName()
                    + " accepted trip " + tripId);

            // Cancel offers to other drivers
            cancelOtherOffers(tripId, driverId);

            return trip;
        }

        /**
         * Cancel offers to other drivers for this trip.
         */
        private void cancelOtherOffers(String tripId, String acceptedDriverId) {
            driverOffers.forEach((driverId, offers) -> {
                if (!driverId.equals(acceptedDriverId)) {
                    offers.remove(tripId);
                }
            });
        }

        /**
         * Driver arrives at pickup location.
         */
        public void driverArrived(String driverId, String tripId) {
            Trip trip = activeTrips.get(tripId);
            if (trip != null && trip.getDriverId().equals(driverId)) {
                trip.driverArrived();
                System.out.println("  [Dispatch] Driver arrived at pickup for trip " + tripId);
            }
        }

        /**
         * Start the ride.
         */
        public void startRide(String driverId, String tripId) {
            Trip trip = activeTrips.get(tripId);
            if (trip != null && trip.getDriverId().equals(driverId)) {
                trip.start();
                System.out.println("  [Dispatch] Ride started: trip " + tripId);
            }
        }

        /**
         * Complete the ride.
         */
        public void completeRide(String driverId, String tripId) {
            Trip trip = activeTrips.get(tripId);
            if (trip != null && trip.getDriverId().equals(driverId)) {
                trip.complete();
                Driver driver = drivers.get(driverId);
                if (driver != null) {
                    driver.setCurrentTripId(null);
                    driver.setStatus(DriverStatus.ONLINE);
                }
                System.out.println("  [Dispatch] Ride completed: trip " + tripId
                        + ", fare=₹" + String.format("%.2f", trip.getFare()));
            }
        }

        /**
         * Cancel a ride.
         */
        public void cancelRide(String tripId) {
            Trip trip = pendingRequests.remove(tripId);
            if (trip == null) {
                trip = activeTrips.get(tripId);
            }
            if (trip != null) {
                trip.cancel();
                String driverId = trip.getDriverId();
                if (driverId != null) {
                    Driver driver = drivers.get(driverId);
                    if (driver != null) {
                        driver.setCurrentTripId(null);
                        driver.setStatus(DriverStatus.ONLINE);
                    }
                }
                System.out.println("  [Dispatch] Trip cancelled: " + tripId);
            }
        }

        private String getRegion(Location location) {
            // Simplified: divide map into 0.1° × 0.1° regions
            int regionLat = (int) (location.latitude() * 10);
            int regionLng = (int) (location.longitude() * 10);
            return "region_" + regionLat + "_" + regionLng;
        }
    }

    // =====================================================
    // 5. DEMONSTRATION
    // =====================================================

    public static void main(String[] args) throws Exception {
        System.out.println("========================================");
        System.out.println("RIDE-HAILING SERVICE DESIGN (Uber-like)");
        System.out.println("========================================\n");

        // Initialize services
        LocationService locationService = new LocationService();
        PricingService pricingService = new PricingService();
        ConcurrentHashMap<String, Driver> drivers = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Trip> activeTrips = new ConcurrentHashMap<>();
        DispatchService dispatchService = new DispatchService(
                locationService, pricingService, drivers, activeTrips);

        // Create drivers and put them online
        System.out.println("--- Drivers Coming Online ---");
        Driver driver1 = new Driver("d1", "Rajesh", VehicleType.ECONOMY);
        Driver driver2 = new Driver("d2", "Priya", VehicleType.ECONOMY);
        Driver driver3 = new Driver("d3", "Amit", VehicleType.COMFORT);
        Driver driver4 = new Driver("d4", "Sneha", VehicleType.PREMIUM);

        drivers.put(driver1.getId(), driver1);
        drivers.put(driver2.getId(), driver2);
        drivers.put(driver3.getId(), driver3);
        drivers.put(driver4.getId(), driver4);

        // Drivers come online
        driver1.setStatus(DriverStatus.ONLINE);
        locationService.updateLocation("d1", new Location(12.9716, 77.5946)); // Rajesh near pickup

        driver2.setStatus(DriverStatus.ONLINE);
        locationService.updateLocation("d2", new Location(12.9352, 77.6245)); // Priya 4km away

        driver3.setStatus(DriverStatus.ONLINE);
        locationService.updateLocation("d3", new Location(12.9250, 77.5938)); // Amit 5km away

        driver4.setStatus(DriverStatus.ONLINE);
        locationService.updateLocation("d4", new Location(12.9780, 77.6400)); // Sneha 3km away

        System.out.println("  " + driver1.getName() + " (Economy) - Online at " + driver1.getCurrentLocation());
        System.out.println("  " + driver2.getName() + " (Economy) - Online at " + driver2.getCurrentLocation());
        System.out.println("  " + driver3.getName() + " (Comfort) - Online at " + driver3.getCurrentLocation());
        System.out.println("  " + driver4.getName() + " (Premium) - Online at " + driver4.getCurrentLocation());

        // Create rider
        Rider rider = new Rider("r1", "Vikram", "+91-9876543210", "UPI");

        // Rider requests a ride
        System.out.println("\n--- Rider Requests Ride ---");
        Location pickup = new Location(12.9716, 77.5946); // MG Road, Bangalore
        Location dropoff = new Location(12.9344, 77.6101); // Lalbagh

        System.out.println("  Rider: " + rider.getName());
        System.out.println("  Pickup: " + pickup);
        System.out.println("  Dropoff: " + dropoff);

        // Update demand for surge pricing
        pricingService.updateSupply("region_129_775", 4);

        // Request ride
        CompletableFuture<Trip> future = dispatchService.requestRide(
                rider, pickup, dropoff, VehicleType.ECONOMY);

        Thread.sleep(500);

        // Accept ride (simulate first driver accepting)
        System.out.println("\n--- Driver Accepts Ride ---");
        Trip trip = dispatchService.acceptRide("d1", "trip_");
        if (trip == null) {
            // Try to find the trip from pending requests - in real scenario
            // we'd have the trip ID. Let's just demonstrate the lifecycle.
            System.out.println("  (Simulating trip lifecycle with a new trip)");
            trip = new Trip("trip_demo_1", rider.getId(), pickup, dropoff);
            trip.accept("d1");
            activeTrips.put(trip.getId(), trip);
        }

        System.out.println("  Trip ID: " + trip.getId());
        System.out.println("  Status: " + trip.getStatus());
        System.out.println("  Driver: " + driver1.getName());
        System.out.println("  Surge: " + trip.getSurgeMultiplier() + "x");

        // Ride lifecycle
        System.out.println("\n--- Ride Lifecycle ---");
        dispatchService.driverArrived("d1", trip.getId());
        Thread.sleep(200);
        dispatchService.startRide("d1", trip.getId());
        Thread.sleep(200);
        dispatchService.completeRide("d1", trip.getId());

        System.out.println("\n  Final Status: " + trip.getStatus());
        System.out.println("  Distance: " + String.format("%.2f", trip.getDistanceKm()) + " km");
        System.out.println("  Duration: " + trip.getDurationMin() + " min");
        System.out.println("  Fare: ₹" + String.format("%.2f", trip.getFare()));

        // Find nearby drivers demo
        System.out.println("\n--- Nearby Drivers (3km from pickup) ---");
        List<String> nearby = locationService.findNearbyDrivers(pickup, 3.0, 10);
        System.out.println("  Found " + nearby.size() + " drivers:");
        for (String driverId : nearby) {
            Driver d = drivers.get(driverId);
            Location loc = locationService.getLocation(driverId);
            System.out.println("    " + d.getName() + " (" + d.getVehicleType()
                    + ") - " + String.format("%.2f", pickup.distanceTo(loc)) + " km away");
        }

        // Pricing demo
        System.out.println("\n--- Fare Estimation ---");
        double economyFare = pricingService.estimateFare(pickup, dropoff, VehicleType.ECONOMY, 1.0);
        double comfortFare = pricingService.estimateFare(pickup, dropoff, VehicleType.COMFORT, 1.2);
        double premiumFare = pricingService.estimateFare(pickup, dropoff, VehicleType.PREMIUM, 1.5);
        System.out.println("  Economy: ₹" + String.format("%.2f", economyFare));
        System.out.println("  Comfort (1.2x): ₹" + String.format("%.2f", comfortFare));
        System.out.println("  Premium (1.5x): ₹" + String.format("%.2f", premiumFare));

        System.out.println("\n========================================");
        System.out.println("KEY DESIGN DECISIONS:");
        System.out.println("========================================");
        System.out.println("• Uber H3 hexagonal grid for spatial indexing");
        System.out.println("• Redis Geospatial (GEOADD/GEORADIUS) for real-time locations");
        System.out.println("• Expanding radius search (3km → 15km)");
        System.out.println("• Offer to top N drivers, first accept wins");
        System.out.println("• Surge pricing based on supply/demand ratio per region");
        System.out.println("• WebSocket for real-time GPS updates (3-5s interval)");
        System.out.println("• Cassandra for trip history (write-optimized)");
        System.out.println("• PostgreSQL for user/driver accounts");
        System.out.println("• Sharding by region/city for horizontal scaling");
        System.out.println("• Kafka for async trip event processing");
        System.out.println("========================================");
    }
}
