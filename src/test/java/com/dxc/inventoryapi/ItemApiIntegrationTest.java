package com.dxc.inventoryapi;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;

import com.dxc.inventoryapi.entity.Item;
import com.dxc.inventoryapi.exception.ItemException;
import com.dxc.inventoryapi.repository.ItemRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.paramnames.ParameterNamesModule;

@SpringJUnitConfig
@SpringBootTest(webEnvironment = WebEnvironment.MOCK,
			classes = SpringBootDataRestApiDemoApplication.class)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemApiIntegrationTest {

	@Autowired
	private MockMvc mvcClient;
	
	@Autowired
	private ItemRepository itemRepository;
	
	private List<Item> testData;

	private static final String API_URL = "/items";

	@BeforeEach
	public void fillTestData() {

		testData = new ArrayList<>();
		testData.add(new Item(101, "RiceOrPaddy", 1690, LocalDate.now()));
		testData.add(new Item(102, "Wheat", 2990, LocalDate.now()));
		testData.add(new Item(103, "Barley", 4790, LocalDate.now()));
		testData.add(new Item(104, "CoffeBean", 7590, LocalDate.now()));
		testData.add(new Item(105, "CocoPowder", 6590, LocalDate.now()));
		
		for(Item item : testData) {
			itemRepository.saveAndFlush(item);
		}
	}

	@AfterEach
	public void clearDatabase() {
		itemRepository.deleteAll();
		testData = null;
	}

	@Test
    public void getAllItemsTest()  {
        try {
            mvcClient.perform(get(API_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andDo(print());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

	@Test
	public void getItemByIdTest() throws ItemException {
		Item testRec = testData.get(0);

		try {
			mvcClient.perform(get(API_URL + "/" + testRec.getIcode())).andExpect(status().isOk())
					.andExpect(jsonPath("$.title", is(testRec.getTitle())))
					.andExpect(jsonPath("$.price", is(testRec.getPrice()))).andDo(print());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}

	}

	@Test
	public void getItemByTitleTest() throws ItemException {
		Item testRec = testData.get(0);

		try {
			mvcClient.perform(get(API_URL + "/" + testRec.getTitle())).andExpect(status().isOk())
					.andExpect(jsonPath("$.icode", is(testRec.getIcode())))
					.andExpect(jsonPath("$.price", is(testRec.getPrice()))).andDo(print());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
    public void getItemByPackageDateTest(){
        Item testRec = testData.get(0);
        
        try {
            mvcClient.perform(get(API_URL + "/" + testRec.getPackageDate()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andDo(print());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }    
	}
	@Test
    public void getItemByPriceRangeTest() {
        
        try {
            mvcClient.perform(get(API_URL + "/priceBetween/1000/and/8000"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(5)))
                .andDo(print());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    }

	// Negative test case
	@Test
	public void getItemByIdTestNonExisting() throws ItemException {

		try {
			mvcClient.perform(get(API_URL + "/8888")).andExpect(status().isNotFound()).andDo(print());
		} catch (Exception e) {
			Assertions.fail(e.getMessage());
		}
	}
	
	private static final ObjectMapper makeMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new ParameterNamesModule());
        mapper.registerModule(new Jdk8Module());
        mapper.registerModule(new JavaTimeModule());
        return mapper;
    }
   
	@Test
    public void createItemTest() {
        Item testRec = new Item(201, "Ground Nuts", 5025, LocalDate.now());
        
        try {    
            mvcClient.perform(post(API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeMapper().writeValueAsString(testRec))
                    ).andExpect(status().isOk()).andDo(print());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
    
    }
   
    @Test
    public void updateItemTest() {
        Item testRec = testData.get(0);
       
        try {
            System.out.println((new ObjectMapper()).writeValueAsString(testRec));
            mvcClient.perform(put(API_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(makeMapper().writeValueAsString(testRec))
                    ).andExpect(status().isOk()).andDo(print());
        } catch (Exception e) {
            Assertions.fail(e.getMessage());
        }
   
    }
	    
	    @Test
	    public void deleteItemsByIdTest() {
	        
	        try {
	            
	            mvcClient.perform(delete(API_URL + "/" + testData.get(0).getIcode()))
	            .andExpect(status().isOk())
	            .andDo(print());
	        } catch (Exception e) {
	            Assertions.fail(e.getMessage());
	        }
	    }	    
	}