package com.beardtrust.webapp.loanservice.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.beardtrust.webapp.loanservice.entities.LoanTypeEntity;
import com.beardtrust.webapp.loanservice.repos.LoanTypeRepository;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {LoanTypeServiceImpl.class})
@ExtendWith(SpringExtension.class)
public class LoanTypeServiceImplTest {
    @MockBean
    private LoanTypeRepository loanTypeRepository;

    @Autowired
    private LoanTypeServiceImpl loanTypeServiceImpl;

    @Test
    public void testSave() {
        LoanTypeEntity loanTypeEntity = new LoanTypeEntity();
        loanTypeEntity.setId("42");
        loanTypeEntity.setTypeName("Type Name");
        loanTypeEntity.setActiveStatus(true);
        loanTypeEntity.setApr(10.0);
        loanTypeEntity.setNumMonths(10);
        loanTypeEntity.setDescription("The characteristics of someone or something");
        when(this.loanTypeRepository.save((LoanTypeEntity) any())).thenReturn(loanTypeEntity);

        LoanTypeEntity loanTypeEntity1 = new LoanTypeEntity();
        loanTypeEntity1.setId("42");
        loanTypeEntity1.setTypeName("Type Name");
        loanTypeEntity1.setActiveStatus(true);
        loanTypeEntity1.setApr(10.0);
        loanTypeEntity1.setNumMonths(10);
        loanTypeEntity1.setDescription("The characteristics of someone or something");
        this.loanTypeServiceImpl.save(loanTypeEntity1);
        verify(this.loanTypeRepository).save((LoanTypeEntity) any());
        assertTrue(loanTypeEntity1.getActiveStatus());
        assertTrue(this.loanTypeServiceImpl.getAll().isEmpty());
    }

    @Test
    public void testSave2() {
        LoanTypeEntity loanTypeEntity = new LoanTypeEntity();
        loanTypeEntity.setId("42");
        loanTypeEntity.setTypeName("Type Name");
        loanTypeEntity.setActiveStatus(true);
        loanTypeEntity.setApr(10.0);
        loanTypeEntity.setNumMonths(10);
        loanTypeEntity.setDescription("The characteristics of someone or something");
        when(this.loanTypeRepository.save((LoanTypeEntity) any())).thenReturn(loanTypeEntity);

        LoanTypeEntity loanTypeEntity1 = new LoanTypeEntity();
        loanTypeEntity1.setId(null);
        loanTypeEntity1.setTypeName("Type Name");
        loanTypeEntity1.setActiveStatus(true);
        loanTypeEntity1.setApr(10.0);
        loanTypeEntity1.setNumMonths(10);
        loanTypeEntity1.setDescription("The characteristics of someone or something");
        this.loanTypeServiceImpl.save(loanTypeEntity1);
        verify(this.loanTypeRepository).save((LoanTypeEntity) any());
        assertTrue(loanTypeEntity1.getActiveStatus());
        assertTrue(this.loanTypeServiceImpl.getAll().isEmpty());
    }

    @Test
    public void testGetAll() {
        ArrayList<LoanTypeEntity> loanTypeEntityList = new ArrayList<LoanTypeEntity>();
        when(this.loanTypeRepository.findAll()).thenReturn(loanTypeEntityList);
        List<LoanTypeEntity> actualAll = this.loanTypeServiceImpl.getAll();
        assertSame(loanTypeEntityList, actualAll);
        assertTrue(actualAll.isEmpty());
        verify(this.loanTypeRepository).findAll();
    }

    @Test
    public void testDeactivate() {
        doNothing().when(this.loanTypeRepository).deactivateById((String) any());
        this.loanTypeServiceImpl.deactivate("42");
        verify(this.loanTypeRepository).deactivateById((String) any());
        assertTrue(this.loanTypeServiceImpl.getAll().isEmpty());
    }

    @Test
    public void testGetAllLoanTypesPage() {
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> this.loanTypeServiceImpl.getAllLoanTypesPage(10, 3, new String[]{"Sort By"}, "Search"));
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> this.loanTypeServiceImpl.getAllLoanTypesPage(10, 3, new String[]{","}, "Search"));
        assertThrows(ArrayIndexOutOfBoundsException.class,
                () -> this.loanTypeServiceImpl.getAllLoanTypesPage(10, 3, new String[]{}, "Search"));
    }
}

