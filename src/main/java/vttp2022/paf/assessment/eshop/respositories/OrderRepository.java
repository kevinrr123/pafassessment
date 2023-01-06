package vttp2022.paf.assessment.eshop.respositories;

import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
}

	

