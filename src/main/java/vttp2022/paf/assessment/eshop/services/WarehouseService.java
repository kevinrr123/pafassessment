package vttp2022.paf.assessment.eshop.services;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import vttp2022.paf.assessment.eshop.models.LineItem;
import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.OrderStatus;

@Service
public class WarehouseService {

	private JdbcTemplate template;

	// You cannot change the method's signature
	// You may add one or more checked exceptions
	public OrderStatus dispatch(Order order) {

		// TODO: Task 4
		
		JsonArrayBuilder builder = Json.createArrayBuilder();
			for (LineItem i : order.getLineItems())
			{
				builder.add(Json.createObjectBuilder()
					   .add("item", i.getItem())
					   .add("quantity",i.getQuantity()));
			}
			JsonObject payload = Json.createObjectBuilder()
					.add("orderId", order.getOrderId())
					.add("name", order.getName())
					.add("address", order.getAddress())
					.add("email", order.getEmail())
					.add("lineItems", builder)
					.add("createdBy", "Kevin Richard Raja")
					.build();
		RequestEntity<String> req = RequestEntity.post("http://paf.chuklee.com/dispatch/{orderId}")
												 .contentType(MediaType.APPLICATION_JSON)
												 .accept(MediaType.APPLICATION_JSON)
												 .body(payload.toString());
	
		RestTemplate rest = new RestTemplate();
		ResponseEntity<String> resp = rest.exchange(req, String.class);
		OrderStatus status = new OrderStatus();
		JsonObject jObj;
		try (InputStream is = new ByteArrayInputStream(resp.getBody().getBytes())){
			JsonReader jr = Json.createReader(is);
			jObj = jr.readObject();
			status.setOrderId(jObj.getString("orderId"));
			status.setDeliveryId(jObj.getString("deliveryId"));
			status.setStatus("dispatched");
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
			template.update("insert into order_status(order_id, delivery_id, status, status_update) values(?,?,?,?)", order.getOrderId(),order.getDeliveryId(),status.getStatus(),timeStamp);
			return status;
		}catch(Exception ex){
			status.setOrderId(order.getOrderId());
			status.setStatus("pending");
			String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new java.util.Date());
			template.update("insert into order_status(order_id, delivery_id, status, status_update) values(?,?,?,?)", order.getOrderId(),order.getDeliveryId(),status.getStatus(),timeStamp);
			return status;
		}
	}
}
