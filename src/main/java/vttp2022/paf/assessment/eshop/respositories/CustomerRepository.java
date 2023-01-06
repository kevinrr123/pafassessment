package vttp2022.paf.assessment.eshop.respositories;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Repository;

import vttp2022.paf.assessment.eshop.models.Customer;
import static vttp2022.paf.assessment.eshop.respositories.Queries.*;

@Repository
public class CustomerRepository {

	@Autowired
    private JdbcTemplate template;

	// You cannot change the method's signature
	public Optional<Customer> findCustomerByName(String name) {
		// TODO: Task 3 

		final SqlRowSet result = template.queryForRowSet(SQL_SELECT_USER_BY_NAME, name);
        
        if (!result.next()){
            return Optional.empty();
        }
		
		Customer user = new Customer();
		user.setName(result.getString("name"));
		user.setAddress(result.getString("address"));
		user.setEmail(result.getString("email"));
		return Optional.of(user);

	}
}
