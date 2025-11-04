FROM eclipse-temurin:25
ADD target/gym-management-zdi.jar gym-management-zdi.jar

ENTRYPOINT ["java", "-jar", "/gym-management-zdi.jar" ]

