package com.dsa.interview;

/**
 * AWS Interview Questions - Concepts & Code Examples
 * 
 * Covers: EC2, S3, Lambda, API Gateway, DynamoDB, RDS, SQS, SNS,
 * CloudFront, Route53, VPC, IAM, CloudFormation, Elastic Beanstalk,
 * ECS/EKS, ElastiCache, Kinesis, Step Functions, CloudWatch
 */
public class AWSInterviewQuestions {

    // =============================================
    // 1. COMPUTE SERVICES
    // =============================================

    /**
     * Q1: EC2 vs Lambda vs ECS/EKS
     * 
     * EC2 (Elastic Compute Cloud):
     * - Virtual machines in the cloud
     * - Full control over OS, software, security
     * - Best for: legacy apps, custom environments, predictable workloads
     * - Pricing: per hour/second (reserved, spot, on-demand)
     * - Scaling: Auto Scaling Groups + Load Balancer
     * 
     * Lambda (Serverless Functions):
     * - Run code without provisioning servers
     * - Max 15 min execution, 10GB memory, 50MB deployment package
     * - Best for: event-driven, microservices, APIs, data processing
     * - Pricing: per invocation + duration (ms)
     * - Scaling: automatic, up to 1000 concurrent executions (default)
     * - Cold start: latency when function hasn't been invoked recently
     * 
     * ECS/EKS (Containers):
     * - Docker containers orchestrated by AWS
     * - ECS: AWS-native, simpler, integrates with AWS services
     * - EKS: Kubernetes, portable, complex
     * - Fargate: serverless containers (no EC2 management)
     * - Best for: microservices, batch jobs, CI/CD pipelines
     */

    /**
     * Q2: EC2 Auto Scaling and Load Balancing
     * 
     * Auto Scaling Components:
     * - Launch Template: AMI, instance type, security groups, user data
     * - Auto Scaling Group: min/max/desired capacity, VPC, subnets
     * - Scaling Policies: target tracking, step scaling, scheduled
     * 
     * ELB (Elastic Load Balancer) Types:
     * - ALB (Application LB): HTTP/HTTPS, path-based routing, host-based routing
     * - NLB (Network LB): TCP/UDP, ultra-low latency, static IP
     * - GWLB (Gateway LB): Layer 3/4, for appliances (firewalls)
     * 
     * Blue/Green Deployment:
     * - Two identical environments (blue = current, green = new)
     * - Route traffic gradually from blue to green
     * - Instant rollback by switching back to blue
     */

    // =============================================
    // 2. STORAGE SERVICES
    // =============================================

    /**
     * Q3: S3 (Simple Storage Service)
     * 
     * Storage Classes:
     * - S3 Standard: frequent access, 99.99% availability
     * - S3 Intelligent-Tiering: auto cost optimization
     * - S3 Standard-IA: infrequent access, lower cost
     * - S3 One Zone-IA: less critical data, lower cost
     * - S3 Glacier: archival (minutes to hours retrieval)
     * - S3 Glacier Deep Archive: cheapest (12 hours retrieval)
     * 
     * S3 Features:
     * - Versioning: keep multiple versions of objects
     * - Lifecycle Policies: auto-transition between storage classes
     * - Replication: cross-region (CRR) or same-region (SRR)
     * - Encryption: SSE-S3, SSE-KMS, SSE-C, client-side
     * - Presigned URLs: temporary access to private objects
     * - Static Website Hosting: host static sites directly from S3
     * - Event Notifications: SNS, SQS, Lambda on object create/delete
     * 
     * S3 Consistency Model:
     * - Strong read-after-write consistency (since Dec 2020)
     * - Previously: eventual consistency for overwrites
     */

    /**
     * Q4: S3 Performance Optimization
     * 
     * - Use multipart upload for large objects (>100MB)
     * - Use S3 Transfer Acceleration for global uploads
     * - Use CloudFront CDN for global downloads
     * - Use S3 Byte-Range Fetches for parallel downloads
     * - Avoid sequential key prefixes (use hash prefixes)
     * - Use S3 Select to retrieve subset of data
     * 
     * // Java SDK - Multipart Upload
     * 
     * AmazonS3 s3Client = AmazonS3ClientBuilder.defaultClient();
     * 
     * // Initiate multipart upload
     * InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(
     *         bucketName, key);
     * InitiateMultipartUploadResult initResponse = s3Client.initiateMultipartUpload(initRequest);
     * 
     * // Upload parts
     * List<PartETag> partETags = new ArrayList<>();
     * for (int i = 0; i < parts.size(); i++) {
     *     UploadPartRequest uploadRequest = new UploadPartRequest()
     *             .withBucketName(bucketName)
     *             .withKey(key)
     *             .withUploadId(initResponse.getUploadId())
     *             .withPartNumber(i + 1)
     *             .withInputStream(parts.get(i))
     *             .withPartSize(partSize);
     *     PartETag eTag = s3Client.uploadPart(uploadRequest).getPartETag();
     *     partETags.add(eTag);
     * }
     * 
     * // Complete multipart upload
     * CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(
     *         bucketName, key, initResponse.getUploadId(), partETags);
     * s3Client.completeMultipartUpload(compRequest);
     */

    // =============================================
    // 3. DATABASE SERVICES
    // =============================================

    /**
     * Q5: RDS vs DynamoDB vs ElastiCache
     * 
     * RDS (Relational Database Service):
     * - SQL databases: MySQL, PostgreSQL, Oracle, SQL Server, MariaDB, Aurora
     * - ACID transactions, joins, complex queries
     * - Multi-AZ for high availability (synchronous standby replica)
     * - Read Replicas for read scaling (up to 15, async)
     * - Automated backups, snapshots, point-in-time recovery
     * 
     * DynamoDB (NoSQL):
     * - Key-value and document database
     * - Single-digit millisecond latency at any scale
     * - Auto-scaling throughput (RCU/WCU)
     * - DAX (DynamoDB Accelerator): in-memory cache (microsecond latency)
     * - Global Tables: multi-region, multi-master replication
     * - TTL: auto-expire items
     * - Transactions: ACID across multiple items (since 2018)
     * - Streams: capture changes for Lambda, Kinesis
     * 
     * ElastiCache:
     * - In-memory caching (Redis or Memcached)
     * - Redis: rich data structures, persistence, replication, pub/sub
     * - Memcached: simple, multi-threaded, no persistence
     * - Use cases: session store, cache, real-time leaderboards
     */

    /**
     * Q6: DynamoDB - Single-Table Design
     * 
     * Key Concepts:
     * - Partition Key (PK): hash key for data distribution
     * - Sort Key (SK): range key for sorting within partition
     * - LSI (Local Secondary Index): different SK on same PK
     * - GSI (Global Secondary Index): different PK+SK
     * 
     * Single-Table Design Pattern:
     * - Store multiple entity types in one table
     * - Use composite keys: PK = "USER#123", SK = "ORDER#456"
     * - Use GSI for access patterns
     * 
     * Example: E-commerce single-table design
     * PK              SK                  Data
     * USER#123        USER#123            {name, email}
     * USER#123        ORDER#456           {orderId, total, status}
     * USER#123        ORDER#789           {orderId, total, status}
     * ORDER#456       ITEM#P001           {productId, qty, price}
     * ORDER#456       ITEM#P002           {productId, qty, price}
     * PRODUCT#P001    PRODUCT#P001        {name, category, price}
     * 
     * Access Patterns:
     * 1. Get user: PK=USER#123, SK=USER#123
     * 2. Get user's orders: PK=USER#123, SK begins_with ORDER#
     * 3. Get order items: PK=ORDER#456, SK begins_with ITEM#
     * 4. Get product: PK=PRODUCT#P001, SK=PRODUCT#P001
     */

    // =============================================
    // 4. MESSAGING & INTEGRATION
    // =============================================

    /**
     * Q7: SQS vs SNS vs Kinesis
     * 
     * SQS (Simple Queue Service):
     * - Pull-based messaging (consumers poll)
     * - At-least-once delivery (standard) or exactly-once (FIFO)
     * - Standard: unlimited TPS, best-effort ordering
     * - FIFO: 3000 TPS (with batching), guaranteed ordering
     * - Visibility timeout: message hidden after polled
     * - Dead Letter Queue (DLQ): failed messages after maxReceiveCount
     * - Delay queues: up to 15 min delay
     * - Max message size: 256KB (use S3 for larger)
     * 
     * SNS (Simple Notification Service):
     * - Push-based pub/sub messaging
     * - Topics with multiple subscribers (SQS, Lambda, HTTP, Email, SMS)
     * - Fan-out pattern: one message → multiple subscribers
     * - Message filtering: subscribers receive only matching messages
     * - FIFO topics: ordered, deduplicated delivery
     * 
     * Kinesis:
     * - Real-time streaming data
     * - Data Streams: shards for scaling, 1MB/s per shard
     * - Data Firehose: load streaming data to S3, Redshift, Elasticsearch
     * - Data Analytics: real-time SQL analytics on streams
     * - Video Streams: streaming video from devices
     * - Retention: 24 hours (default) to 365 days (extended)
     */

    /**
     * Q8: SQS with Lambda - Event-Driven Architecture
     * 
     * // Lambda function triggered by SQS
     * 
     * public class OrderProcessor implements RequestHandler<SQSEvent, Void> {
     *     @Override
     *     public Void handleRequest(SQSEvent event, Context context) {
     *         for (SQSEvent.SQSMessage msg : event.getRecords()) {
     *             try {
     *                 String body = msg.getBody();
     *                 // Process order
     *                 processOrder(body);
     *             } catch (Exception e) {
     *                 // Message will return to queue after visibility timeout
     *                 // After maxReceiveCount, goes to DLQ
     *                 System.err.println("Failed to process: " + e.getMessage());
     *             }
     *         }
     *         return null;
     *     }
     * }
     * 
     * // Lambda DLQ configuration:
     * // If function fails 3 times, message goes to DLQ
     * // DLQ can be SQS or SNS for alerting
     */

    // =============================================
    // 5. NETWORKING & CDN
    // =============================================

    /**
     * Q9: VPC (Virtual Private Cloud)
     * 
     * VPC Components:
     * - Subnets: public (with Internet Gateway) and private (NAT Gateway)
     * - Route Tables: control traffic between subnets
     * - Internet Gateway (IGW): internet access for public subnets
     * - NAT Gateway/Instance: internet access for private subnets
     * - Security Groups: stateful firewall at instance level
     * - NACLs: stateless firewall at subnet level
     * - VPC Peering: connect VPCs (same or different accounts)
     * - Transit Gateway: hub-and-spoke for many VPCs
     * - VPN: connect on-premises to AWS
     * - Direct Connect: dedicated private connection to AWS
     * 
     * Security Groups vs NACLs:
     * - SG: stateful, allow rules only, instance-level
     * - NACL: stateless, allow+deny rules, subnet-level, evaluated in order
     */

    /**
     * Q10: CloudFront and Route53
     * 
     * CloudFront (CDN):
     * - Edge locations: 450+ globally
     * - Caches content at edge (TTL configurable)
     * - Supports HTTP/HTTPS, WebSocket, RTMP
     * - Origin: S3, ALB, EC2, Lambda, custom HTTP
     * - Lambda@Edge: run code at edge locations
     * - Geo-restriction: allow/block countries
     * - DDoS protection via AWS Shield
     * - Signed URLs/Cookies: secure private content
     * 
     * Route53 (DNS):
     * - Routing Policies:
     *   - Simple: single resource
     *   - Weighted: distribute traffic by weight
     *   - Latency: route to lowest latency region
     *   - Geolocation: route based on user location
     *   - Failover: active-passive (health check)
     *   - Multi-value: return multiple healthy resources
     * - Alias Records: map to AWS resources (free)
     * - Health Checks: monitor endpoint health
     * - DNSSEC: DNS security extension
     */

    // =============================================
    // 6. SECURITY & IAM
    // =============================================

    /**
     * Q11: IAM (Identity and Access Management)
     * 
     * IAM Components:
     * - Users: individual people or services
     * - Groups: collection of users (shared permissions)
     * - Roles: assumed by users, services, or federated identities
     * - Policies: JSON documents defining permissions
     * 
     * Policy Structure:
     * {
     *   "Version": "2012-10-17",
     *   "Statement": [{
     *     "Effect": "Allow",
     *     "Action": ["s3:GetObject", "s3:PutObject"],
     *     "Resource": "arn:aws:s3:::my-bucket/*",
     *     "Condition": {
     *       "IpAddress": {"aws:SourceIp": "192.168.1.0/24"}
     *     }
     *   }]
     * }
     * 
     * Least Privilege Principle:
     * - Grant only necessary permissions
     * - Use resource-level permissions
     * - Use conditions (IP, time, MFA, SSL)
     * - Use roles instead of long-term credentials
     * - Rotate keys regularly
     * 
     * IAM Best Practices:
     * - Use IAM roles for EC2 (instance profiles)
     * - Use IAM roles for Lambda (execution roles)
     * - Enable MFA for root and privileged users
     * - Use access keys only for programmatic access
     * - Use AWS Organizations for multi-account management
     * - Use SCP (Service Control Policies) for guardrails
     */

    // =============================================
    // 7. MONITORING & OBSERVABILITY
    // =============================================

    /**
     * Q12: CloudWatch, X-Ray, and CloudTrail
     * 
     * CloudWatch:
     * - Metrics: CPU, memory, disk, custom metrics
     * - Logs: centralize logs from EC2, Lambda, etc.
     * - Alarms: trigger actions based on thresholds
     * - Dashboards: visualize metrics and logs
     * - Synthetics: canary monitoring (simulate user actions)
     * - Contributor Insights: top contributors (IPs, users)
     * 
     * X-Ray (Distributed Tracing):
     * - Trace requests across microservices
     * - Service maps: visualize dependencies
     * - Identify bottlenecks and errors
     * - Annotations for custom metadata
     * - Integrates with Lambda, API Gateway, ECS
     * 
     * CloudTrail (Audit):
     * - Records all API calls (who, what, when, where)
     * - Management events: create/delete resources
     * - Data events: S3 GetObject, Lambda Invoke
     * - Insights: detect unusual activity
     * - Store in S3 for compliance
     */

    // =============================================
    // 8. INFRASTRUCTURE AS CODE
    // =============================================

    /**
     * Q13: CloudFormation vs Terraform vs CDK
     * 
     * CloudFormation (AWS-native):
     * - YAML/JSON templates
     * - Stack-based resource management
     * - Change sets: preview changes before applying
     * - Drift detection: detect manual changes
     * - StackSets: deploy across accounts/regions
     * 
     * Terraform (HashiCorp):
     * - HCL (HashiCorp Configuration Language)
     * - Multi-cloud (AWS, Azure, GCP)
     * - State management (local or remote)
     * - Modules: reusable components
     * - Workspaces: environment separation
     * 
     * CDK (Cloud Development Kit):
     * - Define infrastructure in code (TypeScript, Python, Java, C#)
     * - Higher-level constructs (L1, L2, L3)
     * - Synthesizes to CloudFormation templates
     * - Best for developers who prefer programming languages
     */

    // =============================================
    // 9. SERVERLESS ARCHITECTURE
    // =============================================

    /**
     * Q14: Serverless Application Patterns
     * 
     * Pattern 1: API Gateway + Lambda + DynamoDB (CRUD API)
     * - API Gateway: HTTP endpoints, auth, throttling
     * - Lambda: business logic
     * - DynamoDB: data storage
     * 
     * Pattern 2: S3 + Lambda + SQS (File Processing)
     * - S3: file upload triggers event
     * - Lambda: process file, send to SQS
     * - SQS: decouple processing, handle failures
     * 
     * Pattern 3: EventBridge + Lambda + Step Functions (Workflow)
     * - EventBridge: event bus, routing rules
     * - Step Functions: orchestrate multi-step workflows
     * - Lambda: individual task execution
     * 
     * Pattern 4: Kinesis + Lambda + S3 (Data Analytics)
     * - Kinesis: real-time data ingestion
     * - Lambda: transform and enrich data
     * - S3: data lake storage
     * 
     * Step Functions State Types:
     * - Task: single unit of work (Lambda, Activity)
     * - Choice: branching logic
     * - Parallel: parallel execution
     * - Map: iterate over array
     * - Wait: delay execution
     * - Fail/Succeed: terminal states
     */

    // =============================================
    // 10. HIGH AVAILABILITY & DISASTER RECOVERY
    // =============================================

    /**
     * Q15: HA/DR Strategies
     * 
     * RPO (Recovery Point Objective): max acceptable data loss (time)
     * RTO (Recovery Time Objective): max acceptable downtime
     * 
     * DR Strategies (cost vs recovery time):
     * 1. Backup & Restore (RPO: hours, RTO: 24h+) - Cheapest
     *    - Regular backups to S3, restore when needed
     * 
     * 2. Pilot Light (RPO: minutes, RTO: hours)
     *    - Core services running (DB), scale up when needed
     *    - EC2 instances stopped, AMIs ready
     * 
     * 3. Warm Standby (RPO: seconds, RTO: minutes)
     *    - Scaled-down version running in DR region
     *    - Route53 failover to DR
     * 
     * 4. Multi-Site Active-Active (RPO: 0, RTO: near 0) - Most expensive
     *    - Full production in multiple regions
     *    - Route53 latency/geolocation routing
     * 
     * Multi-AZ vs Multi-Region:
     * - Multi-AZ: within same region, for high availability
     * - Multi-Region: across regions, for disaster recovery
     */

    // =============================================
    // 11. COST OPTIMIZATION
    // =============================================

    /**
     * Q16: AWS Cost Optimization Strategies
     * 
     * Compute:
     * - Use Spot Instances for fault-tolerant workloads (up to 90% savings)
     * - Use Reserved Instances for steady-state workloads (up to 75%)
     * - Use Savings Plans (compute or EC2)
     * - Right-size instances (use Compute Optimizer)
     * - Auto Scaling to match demand
     * 
     * Storage:
     * - S3 Lifecycle Policies to transition to cheaper tiers
     * - Delete incomplete multipart uploads
     * - Use S3 Intelligent-Tiering for unknown patterns
     * - EBS gp3 instead of gp2 (no cost for provisioned IOPS)
     * 
     * Data Transfer:
     * - Use CloudFront to reduce data transfer costs
     * - Use Direct Connect for large data transfers
     * - Use S3 Transfer Acceleration for global uploads
     * - Keep data in same AZ/region when possible
     * 
     * Serverless:
     * - Lambda: provisioned concurrency only when needed
     * - API Gateway: use caching to reduce calls
     * - DynamoDB: auto-scaling, use DAX for read-heavy
     */

    // =============================================
    // MAIN METHOD
    // =============================================

    public static void main(String[] args) {
        System.out.println("AWS INTERVIEW QUESTIONS\n");
        System.out.println("This file contains conceptual AWS interview questions and code examples.\n");
        System.out.println("Topics covered:");
        System.out.println("1. Compute Services (EC2, Lambda, ECS/EKS, Fargate)");
        System.out.println("2. Auto Scaling & Load Balancing (ALB, NLB, GWLB)");
        System.out.println("3. S3 (Storage classes, versioning, lifecycle, encryption)");
        System.out.println("4. S3 Performance (multipart upload, Transfer Acceleration)");
        System.out.println("5. Databases (RDS, DynamoDB, ElastiCache)");
        System.out.println("6. DynamoDB Single-Table Design");
        System.out.println("7. Messaging (SQS, SNS, Kinesis)");
        System.out.println("8. Event-Driven Architecture (SQS + Lambda)");
        System.out.println("9. VPC (subnets, security groups, NACLs, VPN, Direct Connect)");
        System.out.println("10. CloudFront & Route53 (CDN, DNS routing policies)");
        System.out.println("11. IAM (users, groups, roles, policies, least privilege)");
        System.out.println("12. Monitoring (CloudWatch, X-Ray, CloudTrail)");
        System.out.println("13. Infrastructure as Code (CloudFormation, Terraform, CDK)");
        System.out.println("14. Serverless Patterns (API Gateway, Step Functions, EventBridge)");
        System.out.println("15. HA/DR Strategies (RPO/RTO, Multi-AZ, Multi-Region)");
        System.out.println("16. Cost Optimization (Spot, Reserved, Savings Plans)");

        System.out.println("\n================================================");
        System.out.println("DEMONSTRATION COMPLETE");
        System.out.println("================================================");
    }
}
