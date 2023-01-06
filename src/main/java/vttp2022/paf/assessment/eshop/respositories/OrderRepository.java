package vttp2022.paf.assessment.eshop.respositories;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import vttp2022.paf.assessment.eshop.models.Order;
import vttp2022.paf.assessment.eshop.models.LineItem;

@Repository
public class OrderRepository {
	// TODO: Task 3

	@Autowired
	private JdbcTemplate template;

	@Transactional(rollbackFor = OrderException.class)
	public String add(Order order, String name) throws Exception{

		try {
            if (name == null) {
                throw new OrderException("failed");
            }
			template.update("insert into order(orderid, name, deliveryid, address, email, orderdate) values(?,?,?,?,?,?)", order.getOrderId(),name,order.getDeliveryId(),order.getAddress(),order.getEmail(),order.getOrderDate());

			List<LineItem> lineitems = order.getLineItems();
			List<Object []> data = new LinkedList<>();
			for(LineItem i: lineitems){
			Object[] l = new Object[3];
			l[0] = i.getItem();
			l[1] = i.getQuantity();
			l[2] = order.getOrderId();
			data.add(l);
			}

			template.batchUpdate("insert into lineitem(item, quantity, orderid) values (?, ?, ?)", data);

			return "success";

        } catch (Exception ex) {
            throw ex;
        }

	}

	public Optional<MultiValueMap<String, Integer>> get(String name) throws Exception{
		MultiValueMap<String, Integer> orders = new LinkedMultiValueMap<>();
		SqlRowSet result = template.queryForRowSet(
				"select count(status) as ordercount from order_status as o join orders as os on o.order_id = os.orderid where os.name = '?' group by status;",name);
		if (orders.size() > 0)
			return Optional.of(orders);
		return Optional.empty();
	
	}
}

	

