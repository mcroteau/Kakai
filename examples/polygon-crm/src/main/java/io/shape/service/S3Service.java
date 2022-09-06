package io.shape.service;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import io.kakai.annotate.Property;
import io.kakai.annotate.Service;

import java.io.InputStream;

@Service
public class S3Service {

    @Property("aws.bucket")
    private String bucket;

    public PutObjectResult send(String name, InputStream stream){
        try {
            Regions region = Regions.US_EAST_1;
            AmazonS3 s3 = AmazonS3ClientBuilder.standard()
                    .withRegion(region)
                    .build();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(stream.available());

            PutObjectRequest putObj = new PutObjectRequest(bucket, name, stream, metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead);
            return s3.putObject(putObj);

        }catch(Exception ex){
            ex.printStackTrace();
        }
        return null;
    }
}
