package codegreed_devs.com.igasvendor.models;

public class OrderModel {

    private String orderId;
    private String clientId;
    private String vendorId;
    private String gasBrand;
    private String gasSize;
    private String gasType;
    private String price;
    private String numberOfCylinders;
    private String orderStatus;

    public OrderModel(String orderId, String clientId, String vendorId, String gasBrand, String gasSize, String gasType,
                      String price, String numberOfCylinders, String orderStatus) {
        this.orderId = orderId;
        this.clientId = clientId;
        this.vendorId = vendorId;
        this.gasBrand = gasBrand;
        this.gasSize = gasSize;
        this.gasType = gasType;
        this.price = price;
        this.numberOfCylinders = numberOfCylinders;
        this.orderStatus = orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getClientId() {
        return clientId;
    }

    public String getVendorId() {
        return vendorId;
    }

    public String getPrice() {
        return price;
    }

    public String getGasBrand() {
        return gasBrand;
    }

    public String getGasSize() {
        return gasSize;
    }

    public String getGasType() {
        return gasType;
    }

    public String getNumberOfCylinders() {
        return numberOfCylinders;
    }

    public String getOrderStatus() {
        return orderStatus;
    }
}
