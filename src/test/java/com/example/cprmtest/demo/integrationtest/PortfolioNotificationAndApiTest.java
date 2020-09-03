package com.example.cprmtest.demo.integrationtest;

import com.example.cprmtest.demo.model.dao.CustomerDao;
import com.example.cprmtest.demo.model.dto.CustomerNotification;
import com.example.cprmtest.demo.model.entities.Customer;
import com.example.cprmtest.demo.portfolio.services.notifier.PushServiceWrapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
@Import(NotificationTestConfig.class)
public class PortfolioNotificationAndApiTest {

    @Autowired
    CustomerDao customerDao;

    @Autowired
    PushServiceWrapper<Double> pushServiceWrapper;

    @Autowired
    private MockMvc mockMvc;

    String c1_url = "http://localhost:8086/samplenotificationendpoint/1";
    String c2_url = "http://localhost:8086/samplenotificationendpoint/2";
    //based on fixed stock inception value
    double c1_initNav = 44000.0;
    double c2_initNav = 17600.0;

    @Test
    public void integrationTest() throws Exception {
        long c1_id = customerDao.findByUrl(c1_url).getId();
        long c2_id = customerDao.findByUrl(c2_url).getId();

        ArgumentCaptor<CustomerNotification<Double>> notificationArgumentCaptor = ArgumentCaptor.forClass(CustomerNotification.class);
        ArgumentCaptor<String> urlArgumentCaptor = ArgumentCaptor.forClass(String.class);

        Thread.sleep(3000); //let price source + services run

        Mockito.verify(pushServiceWrapper, Mockito.atLeast(2)).sendAndCheck(urlArgumentCaptor.capture(), notificationArgumentCaptor.capture());
        // check if c1,c2 notified with their respective notifications
        Assertions.assertTrue(urlArgumentCaptor.getAllValues().contains(c1_url));
        Assertions.assertEquals(c1_id, notificationArgumentCaptor.getAllValues().get(urlArgumentCaptor.getAllValues().indexOf(c1_url)).getCustomerId());
        Assertions.assertTrue(urlArgumentCaptor.getAllValues().contains(c2_url));
        Assertions.assertEquals(c2_id, notificationArgumentCaptor.getAllValues().get(urlArgumentCaptor.getAllValues().indexOf(c2_url)).getCustomerId());
        //see whether nav values generated are within reasonable range (predictable as we have fixed random values)
        List<Double> c1_vals = notificationArgumentCaptor.getAllValues().stream().filter(notif -> notif.getCustomerId() == c1_id).map(CustomerNotification::getPayload).collect(Collectors.toList());
        List<Double> c2_vals = notificationArgumentCaptor.getAllValues().stream().filter(notif -> notif.getCustomerId() == c2_id).map(CustomerNotification::getPayload).collect(Collectors.toList());
        Assertions.assertEquals(c1_vals.size(), c1_vals.stream().filter(val -> val >= c1_initNav && val <= c1_initNav+1000).count());
        Assertions.assertEquals(c2_vals.size(), c2_vals.stream().filter(val -> val >= c2_initNav && val <= c2_initNav+1000).count());
    }


    @Test
    public void testNavEndpoint() throws Exception {
        // check if endpoint works + if c1 nav is within reasonable value
        Customer c = customerDao.findByUrl(c1_url);
        MvcResult result = this.mockMvc.perform(get("/portfolio/" + c.getId())).andReturn();
        Assertions.assertTrue(c1_initNav + 1000 > Double.parseDouble(result.getResponse().getContentAsString()));

        // check if endpoint works + if c2 nav is within reasonable value
        Customer c2 = customerDao.findByUrl(c2_url);
        MvcResult result2 = this.mockMvc.perform(get("/portfolio/" + c2.getId())).andReturn();
        Assertions.assertTrue(c2_initNav + 1000 > Double.parseDouble(result2.getResponse().getContentAsString()));
    }
}
