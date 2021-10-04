package com.beardtrust.webapp.loanservice.controllers;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.services.LoanTypeService;
import com.beardtrust.webapp.loanservice.services.LoanTypeServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ContextConfiguration(classes = {LoanTypeController.class})
@ExtendWith(SpringExtension.class)
public class LoanTypeControllerTest {
    @Autowired
    private LoanTypeController loanTypeController;

    @MockBean
    private LoanTypeService loanTypeService;


    @Test
    public void testConstructor() {
        LoanTypeServiceImpl loanTypeServiceImpl = new LoanTypeServiceImpl();
        LoanTypeService loanTypeService = (new LoanTypeController(loanTypeServiceImpl)).loanTypeService;
        assertTrue(loanTypeService instanceof LoanTypeServiceImpl);
        assertSame(loanTypeService, loanTypeServiceImpl);
    }

    @Test
    public void testGetAllLoanTypesPage() throws Exception {
        when(this.loanTypeService.getAllLoanTypesPage(anyInt(), anyInt(), (String[]) any(), (String) any()))
                .thenReturn(new PageImpl<LoanTypeEntity>(new ArrayList<LoanTypeEntity>()));
        MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/loantypes");
        MockHttpServletRequestBuilder paramResult = getResult.param("page", String.valueOf(1)).param("search", "foo");
        MockHttpServletRequestBuilder paramResult1 = paramResult.param("size", String.valueOf(1));
        MockHttpServletRequestBuilder requestBuilder = paramResult1.param("sortBy",
                String.valueOf(new String[]{"foo", "foo", "foo"}));
        MockMvcBuilders.standaloneSetup(this.loanTypeController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testCreateLoanType() throws Exception {
        when(this.loanTypeService.getAllLoanTypesPage(anyInt(), anyInt(), (String[]) any(), (String) any()))
                .thenReturn(new PageImpl<LoanTypeEntity>(new ArrayList<LoanTypeEntity>()));

        LoanTypeEntity loanTypeEntity = new LoanTypeEntity();
        loanTypeEntity.setId("42");
        loanTypeEntity.setTypeName("Type Name");
        loanTypeEntity.setActiveStatus(true);
        loanTypeEntity.setApr(10.0);
        loanTypeEntity.setNumMonths(10);
        loanTypeEntity.setDescription("The characteristics of someone or something");
        String content = (new ObjectMapper()).writeValueAsString(loanTypeEntity);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/loantypes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(this.loanTypeController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeactivateLoanType() throws Exception {
        doNothing().when(this.loanTypeService).deactivate((String) any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/loantypes/{id}", "42");
        MockMvcBuilders.standaloneSetup(this.loanTypeController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testDeactivateLoanType2() throws Exception {
        doNothing().when(this.loanTypeService).deactivate((String) any());
        MockHttpServletRequestBuilder deleteResult = MockMvcRequestBuilders.delete("/loantypes/{id}", "42");
        deleteResult.contentType("Not all who wander are lost");
        MockMvcBuilders.standaloneSetup(this.loanTypeController)
                .build()
                .perform(deleteResult)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetAllLoanTypes() throws Exception {
        when(this.loanTypeService.getAll()).thenReturn(new ArrayList<LoanTypeEntity>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/loantypes/all");
        MockMvcBuilders.standaloneSetup(this.loanTypeController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    public void testGetAllLoanTypes2() throws Exception {
        when(this.loanTypeService.getAll()).thenReturn(new ArrayList<LoanTypeEntity>());
        MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/loantypes/all");
        getResult.contentType("Not all who wander are lost");
        MockMvcBuilders.standaloneSetup(this.loanTypeController)
                .build()
                .perform(getResult)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    public void testUpdateLoanType() throws Exception {
        when(this.loanTypeService.getAllLoanTypesPage(anyInt(), anyInt(), (String[]) any(), (String) any()))
                .thenReturn(new PageImpl<LoanTypeEntity>(new ArrayList<LoanTypeEntity>()));

        LoanTypeEntity loanTypeEntity = new LoanTypeEntity();
        loanTypeEntity.setId("42");
        loanTypeEntity.setTypeName("Type Name");
        loanTypeEntity.setActiveStatus(true);
        loanTypeEntity.setApr(10.0);
        loanTypeEntity.setNumMonths(10);
        loanTypeEntity.setDescription("The characteristics of someone or something");
        String content = (new ObjectMapper()).writeValueAsString(loanTypeEntity);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/loantypes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
        MockMvcBuilders.standaloneSetup(this.loanTypeController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}

