package vttp2022.paf.assessment.eshop.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import vttp2022.paf.assessment.eshop.models.Customer;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;
import vttp2022.paf.assessment.eshop.respositories.CustomerRepository;
import vttp2022.paf.assessment.eshop.respositories.OrderRepository;
import vttp2022.paf.assessment.eshop.services.WarehouseService;

@Controller
@RequestMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces=MediaType.APPLICATION_JSON_VALUE)
public class OrderController {

	//TODO: Task 3

	@Autowired 
	private CustomerRepository cusRepo;

	@Autowired 
	private OrderRepository orderRepo;

	@Autowired
	private WarehouseService whSvc;

	@PostMapping(path="/orders", consumes=MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public ResponseEntity<String> getOrders(@RequestBody MultiValueMap<String, String> AllOrders) throws Exception{
        
		List<LineItem> list = new ArrayList<>();
        LineItem task = new LineItem();
		String name = AllOrders.getFirst("name");
		Optional<Customer> opt = cusRepo.findCustomerByName(name);
		if(opt.isEmpty()){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{error: Customer" + name + "not found}");
        }
		
		Customer customer = opt.get();

        int i = 0;
        while(true){
            String item = AllOrders.getFirst("item-%d".formatted(i));
            if((item == null) || item.trim().length() == 0){
                break;
            }
            Integer quantity = Integer.parseInt(AllOrders.getFirst("quantity-%d".formatted(i)));

            task.setItem(item);
            task.setQuantity(quantity);
            list.add(task);
            i++;
        }

		Order order = new Order();
		order.setName(name);
		String orderId = UUID.randomUUID().toString().substring(0, 8);
        order.setOrderId(orderId);
		order.setDeliveryId(UUID.randomUUID().toString().substring(0,32));
		order.setAddress(customer.getAddress());
		order.setEmail(customer.getEmail());
		order.setLineItems(list);

		try{
			orderRepo.add(order, name);
			OrderStatus status = whSvc.dispatch(order);
			if (status.getStatus().equals("dispatched")){
			JsonObject response2 = Json.createObjectBuilder().add("orderId", order.getOrderId())
															.add("deliveryId", order.getDeliveryId())
															.add("status", "dispatched").build();	
            return ResponseEntity.ok(response2.toString());   
			} else{
			JsonObject response2 = Json.createObjectBuilder().add("orderId", order.getOrderId())
															 .add("status", "pending").build();	
            return ResponseEntity.ok(response2.toString());   
			}
			
		} catch (Exception e){
			e.printStackTrace();
			return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{error: save failed. Please try again.}");
		}

	}

	@GetMapping(path="/api/order/{name}/status", consumes=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> getOrders(@PathVariable String name) throws Exception {
		Optional<MultiValueMap<String, Integer>> result = orderRepo.get(name);
		return ResponseEntity
					.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.contentType(MediaType.APPLICATION_JSON)
					.body("{error: save failed. Please try again.}");
	}

}
