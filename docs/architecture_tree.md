# Arquitectura del proyecto Inventario

Esta es la estructura refactorizada del proyecto, organizada con arquitectura hexagonal y separación por módulo funcional:

```text
src/main/java/com/pragma/Inventario
├── InventarioApplication.java
├── shared
│   └── web
│       ├── advice
│       │   └── ApiExceptionHandler.java
│       └── error
│           └── ErrorResponse.java
├── producto
│   ├── domain
│   │   └── model
│   │       └── Producto.java
│   ├── application
│   │   ├── exception
│   │   │   └── ProductNotFoundException.java
│   │   ├── ports
│   │   │   ├── in
│   │   │   │   └── ProductoUseCase.java
│   │   │   └── out
│   │   │       └── ProductoRepositoryPort.java
│   │   └── service
│   │       └── ProductoApplicationService.java
│   └── infrastructure
│       └── adapters
│           ├── in
│           │   ├── rest
│           │   │   ├── controller
│           │   │   │   └── ProductoRestController.java
│           │   │   └── dto
│           │   │       ├── request
│           │   │       │   ├── CreateProductRequest.java
│           │   │       │   └── UpdateProductRequest.java
│           │   │       └── response
│           │   │           └── ProductResponse.java
│           │   └── web
│           │       ├── ProductoController.java
│           │       ├── form
│           │       │   └── ProductoForm.java
│           │       └── mapper
│           │           └── ProductoMapper.java
│           └── out
│               └── persistence
│                   ├── ProductoEntity.java
│                   ├── SpringDataProductoRepository.java
│                   └── JpaProductoRepositoryAdapter.java
├── security
│   ├── domain
│   │   └── model
│   │       └── User.java
│   ├── application
│   │   ├── exception
│   │   │   ├── UserAlreadyExistsException.java
│   │   │   └── UserNotFoundException.java
│   │   ├── ports
│   │   │   ├── in
│   │   │   │   └── UserManagementUseCase.java
│   │   │   └── out
│   │   │       ├── UserRepositoryPort.java
│   │   │       └── PasswordHasherPort.java
│   │   └── service
│   │       └── UserManagementService.java
│   └── infrastructure
│       ├── config
│       │   └── SecurityConfig.java
│       └── adapters
│           ├── in
│           │   ├── rest
│           │   │   ├── controller
│           │   │   │   └── UserRestController.java
│           │   │   └── dto
│           │   │       ├── request
│           │   │       │   ├── CreateUserRequest.java
│           │   │       │   └── UpdateUserRequest.java
│           │   │       └── response
│           │   │           └── UserResponse.java
│           │   ├── web
│           │   │   ├── AdminUserController.java
│           │   │   └── form
│           │   │       └── UserForm.java
│           │   └── web
│           │       └── controller
│           │           ├── AuthController.java
│           │           ├── HomeController.java
│           │           └── MainController.java
│           │   └── mapper
│           │       └── UserMapper.java
│           ├── out
│           │   ├── persistence
│           │   │   ├── UserEntity.java
│           │   │   ├── SpringDataUserRepository.java
│           │   │   └── JpaUserRepositoryAdapter.java
│           │   └── security
│           │       └── SpringPasswordHasherAdapter.java
│           └── security
│               └── adapters
│                   └── in
│                       └── security
│                           ├── CustomUserDetailsService.java
│                           └── DataInitializer.java
```

## Cómo funciona esta estructura

- `domain`: contiene el modelo de negocio y no depende de Spring ni JPA.
- `application`: concentra casos de uso, reglas de aplicación y excepciones del negocio.
- `shared`: aloja piezas transversales que sí comparten módulos, como el handler global de errores y el DTO `ErrorResponse`.
- `infrastructure/adapters/in/rest`: expone la API REST con DTOs request/response separados por módulo.
- `infrastructure/adapters/in/web`: mantiene las vistas Thymeleaf y sus forms.
- `infrastructure/adapters/out/persistence`: conecta con PostgreSQL mediante JPA.
- `infrastructure/adapters/out/security`: contiene la adaptación de password hashing.

## Flujo de la aplicación

1. El cliente, navegador o Postman envía una petición.
2. El controller del módulo recibe la solicitud.
3. El controller usa un mapper para convertir la entrada al dominio o al comando correcto.
4. El service de aplicación ejecuta la regla de negocio.
5. El adapter de salida persiste o consulta en la base de datos.
6. El resultado vuelve como vista HTML o como JSON REST.
7. Si ocurre un error, `ApiExceptionHandler` lo convierte en `ErrorResponse`.

## Resultado de la refactorización

- Se eliminó el paquete global de controladores web.
- Los DTOs REST quedaron separados de los forms de Thymeleaf.
- Las excepciones se movieron a la capa de aplicación por módulo.
- El manejo de errores quedó centralizado en `shared/web`.
- La conversión entre capas quedó concentrada en mappers dedicados.
