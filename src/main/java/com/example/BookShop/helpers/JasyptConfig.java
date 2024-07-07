package com.example.BookShop.helpers;
import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
@Configuration
@EnableEncryptableProperties
public class JasyptConfig {
    @Bean("jasyptStringEncryptor")
    public StringEncryptor stringEncryptor() {
        StandardPBEStringEncryptor encryptor = new StandardPBEStringEncryptor();
        encryptor.setPassword("tuongkhangivs"); // Mật khẩu để giải mã, phải khớp với mật khẩu dùng để mã hóa
        return encryptor;
    }
}
