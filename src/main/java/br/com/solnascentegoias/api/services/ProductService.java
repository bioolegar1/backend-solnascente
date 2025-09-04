package br.com.solnascentegoias.api.services;

import br.com.solnascentegoias.api.dtos.ProductRequestDTO;
import br.com.solnascentegoias.api.dtos.ProductResponseDTO;
import br.com.solnascentegoias.api.entities.Product;
import br.com.solnascentegoias.api.entities.ProductImage;
import br.com.solnascentegoias.api.repositories.ProductRepository;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors; // Import necessário

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MinioClient minioClient;
    private final String bucketName;
    private final String minioEndpoint;

    public ProductService(ProductRepository productRepository,
                          MinioClient minioClient,
                          @Value("${minio.bucket-name}") String bucketName,
                          @Value("${minio.endpoint}") String minioEndpoint) {
        this.productRepository = productRepository;
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.minioEndpoint = minioEndpoint;
    }

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO productDTO, List<MultipartFile> files) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());
        Product savedProduct = productRepository.save(product);

        int displayOrder = 1;
        for (MultipartFile file : files) {
            try {
                String fileName = savedProduct.getId() + "-" + UUID.randomUUID() + "-" + file.getOriginalFilename();

                minioClient.putObject(
                        PutObjectArgs.builder()
                                .bucket(bucketName)
                                .object(fileName)
                                .stream(file.getInputStream(), file.getSize(), -1)
                                .contentType(file.getContentType())
                                .build()
                );

                String imageUrl = String.format("%s/%s/%s", this.minioEndpoint, this.bucketName, fileName);

                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(imageUrl);
                productImage.setDisplayOrder(displayOrder++);
                savedProduct.addImage(productImage);

            } catch (Exception e) {
                throw new RuntimeException("Erro ao fazer upload do arquivo: " + e.getMessage(), e);
            }
        }

        Product finalProduct = productRepository.save(savedProduct);
        return ProductResponseDTO.fromEntity(finalProduct);
    }


    @Transactional(readOnly = true) // readOnly = true é uma otimização para consultas
    public List<ProductResponseDTO> findAllProducts() {
        return productRepository.findAll().stream()
                .map(ProductResponseDTO::fromEntity)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public ProductResponseDTO findProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
        return ProductResponseDTO.fromEntity(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id) );

        for (ProductImage image : product.getImages()) {
            try {
                String objectName = image.getImageUrl().substring(image.getImageUrl().lastIndexOf("/") + 1);

                minioClient.removeObject(
                        io.minio.RemoveObjectArgs.builder()
                                .bucket(bucketName)
                                .object(objectName)
                                .build()
                );
            }catch (Exception e) {
                System.err.println("Erro ao remover arquivo do Minio: " + e.getMessage());
            }
        }
        productRepository.delete(product);
    }

    @Transactional
    public ProductResponseDTO updateProduct(Long id, ProductRequestDTO productDTO, List<MultipartFile> files) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));

        product.setName(productDTO.getName());
        product.setDescription(productDTO.getDescription());

        if(files != null && !files.isEmpty() && !files.get(0).isEmpty()) {
            for (ProductImage image : product.getImages()) {
                try {
                    String objectName = image.getImageUrl().substring(image.getImageUrl().lastIndexOf("/") + 1);
                    minioClient.removeObject(
                            io.minio.RemoveObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(objectName)
                                    .build()
                    );
                }catch (Exception e) {
                    System.err.println("Erro ao remover arquivo do MinIO: " + e.getMessage());
                }
            }
            product.getImages().clear();

            int displayOrder = 1;
            for (MultipartFile file : files) {
                try {
                    String fileName = product.getId() + "-" + UUID.randomUUID() + "-" + file.getOriginalFilename();
                    minioClient.putObject(
                            PutObjectArgs.builder()
                                    .bucket(bucketName)
                                    .object(fileName)
                                    .stream(file.getInputStream(), file.getSize(), -1)
                                    .contentType(file.getContentType())
                                    .build());

                    String imageUrl = String.format("%s/%s/%s", this.minioEndpoint, this.bucketName, fileName);
                    ProductImage productImage = new ProductImage();
                    productImage.setImageUrl(imageUrl);
                    productImage.setDisplayOrder(displayOrder++);
                    product.addImage(productImage);

                } catch (Exception e) {
                    throw new RuntimeException("Erro ao fazer upload do arquivo: " + e.getMessage(), e);
                }
            }

        }

        Product updatedProduct = productRepository.save(product);
        return ProductResponseDTO.fromEntity(updatedProduct);

    }


}