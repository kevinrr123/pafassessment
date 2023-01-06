package vttp2022.paf.assessment.eshop.respositories;

public class OrderException extends Exception{
    public OrderException(){
        super();
    }

    public OrderException(String message){
        super(message);
    }
}
