package dev.harmeetsingh.caradverts.repository

import com.amazonaws.auth.{AWSStaticCredentialsProvider, BasicAWSCredentials}
import com.amazonaws.client.builder.AwsClientBuilder
import com.amazonaws.services.dynamodbv2.local.main.ServerRunner
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer
import com.amazonaws.services.dynamodbv2.{AmazonDynamoDB, AmazonDynamoDBClientBuilder}
import org.scalatest.{AsyncFlatSpec, BeforeAndAfterAll, Matchers}

trait DynamoTestTrait extends AsyncFlatSpec with BeforeAndAfterAll with Matchers
{
    protected var client : AmazonDynamoDB = _
    protected var server : DynamoDBProxyServer = _
    
    override def beforeAll() : Unit = {
        val dynamoDBPort = "7000"
        val localArgs = Array("-inMemory", "-sharedDb", "1", "-port", dynamoDBPort)
    
        server = ServerRunner.createServerFromCommandLineArgs(localArgs)
        
        System.setProperty("sqlite4java.library.path", "native-libs")
        System.setProperty("aws.accessKeyId", "access_key_id")
        System.setProperty("aws.secretKey", "secret_access_key")
        
        val awsCredentials = new BasicAWSCredentials("dummy", "credentials")
    
        client = AmazonDynamoDBClientBuilder.standard()
            .withCredentials(new AWSStaticCredentialsProvider(awsCredentials))
            .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(s"http://localhost:$dynamoDBPort", ""))
            .build()
    
        server.start()
    }
    
    override def afterAll() : Unit = {
        client.shutdown()
        server.stop()
    }
}