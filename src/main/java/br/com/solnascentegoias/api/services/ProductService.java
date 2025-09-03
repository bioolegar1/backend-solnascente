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

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final MinioClient minioClient;
    private final String bucketName;
    private final String minioEndpoint;


    public ProductService(ProductRepository productRepository,
                          MinioClient minioClient,
                          @Value("${minio.bucket-name}") String bucketName,
                          @Value("${minio.bucket.endpoint") String minioEndpoint) {

        this.productRepository = productRepository;
        this.minioClient = minioClient;
        this.bucketName = bucketName;
        this.minioEndpoint = minioEndpoint;
    }



    @Transactional //tudo funciona ou nada é salvo no banco
    public ProductResponseDTO createProduct(ProductRequestDTO productDTO, List<MultipartFile> files){

        //Dados do Produto.
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setDesciption(productDTO.getDesciption());
        product.setDesciption(productDTO.getDesciption());
        Product savedProduct = productRepository.save(product);

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
                String imageUrl = String.format("%s/%s/%s", this.minioEndpoint, this.bucketName, fileName);

                //Cria  e salva a entidade  ProductImage
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(imageUrl);
                productImage.setDisplayOrder(discplayOrder++);
                savedProduct.addImage(productImage);

            } catch (Exception e) {
                //caso aja erro, lança uma exceção para o Spring reverter a transação
                throw new RuntimeException("Erro ao fazer upload do arquivo" + e.getMessage(), e);
            }

        }
        //salva o produto novamente com as img associadas
        Product finalProduct = productRepository.save(savedProduct);
        //retorna o DTO de resposta
        return ProductResponseDTO.fromEntity(finalProduct);

    }

    public List<ProductResponseDTO> listAllProducts(){
        return productRepository.findAll().stream()
                .map(ProductResponseDTO::fromEntity)
                .toList();
    }

    public ProductResponseDTO findProductById(long id) {
        Product product =  productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado com id: " + id));
        return ProductResponseDTO.fromEntity(product);
    }

}

