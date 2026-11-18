# BÀI THỰC HÀNH SỐ 5: DỊCH VỤ BÁO CÁO (REPORTING - SOA)

## Cách chạy

```powershell
cd D:\kien_truc_huong_dich_vu\soa-lab5-reporting-service
.\mvnw.cmd spring-boot:run
```

Chạy test:

```powershell
.\mvnw.cmd test
```

## Kiểm thử

- Swagger UI: http://localhost:8083/swagger-ui/index.html
- H2 Console: http://localhost:8083/h2-console (`JDBC URL`: `jdbc:h2:mem:reportdb` | `User`: `sa` | `Password`: để trống)
- Lấy JWT Token: gọi `POST http://localhost:8080/login` với `userName: "admin"`, `password: "MTIzNDU2"`, sau đó dán token vào nút **Authorize** trên Swagger UI.

## Danh sách API

### 1. Báo cáo theo sản phẩm (`/reports/products`)

| STT | Phương thức | URL                      | Mô tả                                                      | Quyền truy cập |
| :-: | :---------: | :----------------------- | :--------------------------------------------------------- | :------------: |
|  1  |    `GET`    | `/reports/products`      | Lấy danh sách báo cáo theo sản phẩm (hỗ trợ `?productId=`) | Cần JWT Token  |
|  2  |    `GET`    | `/reports/products/{id}` | Lấy chi tiết báo cáo của một sản phẩm                      | Cần JWT Token  |
|  3  |   `POST`    | `/reports/products`      | Tạo báo cáo sản phẩm từ dữ liệu Product và Order Service   | Cần JWT Token  |
|  4  |  `DELETE`   | `/reports/products/{id}` | Xóa báo cáo sản phẩm                                       | Cần JWT Token  |

### 2. Báo cáo theo đơn hàng (`/reports/orders`)

| STT | Phương thức | URL                    | Mô tả                                                    | Quyền truy cập |
| :-: | :---------: | :--------------------- | :------------------------------------------------------- | :------------: |
|  5  |    `GET`    | `/reports/orders`      | Lấy danh sách báo cáo theo đơn hàng                      | Cần JWT Token  |
|  6  |    `GET`    | `/reports/orders/{id}` | Lấy chi tiết báo cáo của một đơn hàng                    | Cần JWT Token  |
|  7  |   `POST`    | `/reports/orders`      | Tạo báo cáo đơn hàng từ dữ liệu Order và Product Service | Cần JWT Token  |
|  8  |  `DELETE`   | `/reports/orders/{id}` | Xóa báo cáo đơn hàng                                     | Cần JWT Token  |
