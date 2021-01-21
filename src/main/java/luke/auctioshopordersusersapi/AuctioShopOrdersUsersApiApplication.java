package luke.auctioshopordersusersapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

@SpringBootApplication
@EnableEurekaClient
public class AuctioShopOrdersUsersApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(AuctioShopOrdersUsersApiApplication.class, args);
    }

}
