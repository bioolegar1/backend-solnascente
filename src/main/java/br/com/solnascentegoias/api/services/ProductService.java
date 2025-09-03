package br.com.solnascentegoias.api.services;

import br.com.solnascentegoias.api.dtos.ProductRequestDTO;
import br.com.solnascentegoias.api.dtos.ProductResponseDTO;
import br.com.solnascentegoias.api.entities.Product;
import br.com.solnascentegoias.api.repositories.ProductRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucketName;

    public ProductService(ProductRepository productRepository, MinioClient minioClient) {
        this.productRepository = productRepository;
        this.minioClient = minioClient;
    }

    @Transactional //tudo funciona ou nada é salvo no banco
    public ProductResponseDTO createProduct(ProductRequestDTO productDTO, List<MultipartFile> files){

        //Dados do Produto.
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDesciption(productDTO.getDesciption());
        product.setDesciption(productDTO.getDesciption());
        Product saveProduct = productRepository.save(product);

        //Upload das Imagens
        int discplayOrder = 1;
        for (MultipartFile file : files) {
            try{
                //Gera id unico para evitar conflitos
                String fileName = savedProduct.getId() + "-" + UUID.randomUUID() + "-" + file.getOriginalFilename();

                //upload para o MinIO
                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(file.getInputStream(),file.getSize(), -1)
                                .contentType(file.getContentType()).build()
                );

                //PUBLIC URL
                String imageUrl = String.format("%s/%s/%s", minioClient.getEndpoint(), bucketName, fileName);


            }
        }
    }
}
