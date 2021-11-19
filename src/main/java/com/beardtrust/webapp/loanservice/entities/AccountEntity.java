/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.beardtrust.webapp.loanservice.entities;


import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * @author Nathanael <Nathanael.Grier at your.org>
 */
@Entity
@Table(name = "accounts")
public class AccountEntity extends FinancialAsset {
	private static final long serialVersionUID = -3465065516553281959L;

	private Integer interest;
	private String nickname;
	@ManyToOne
	@JoinColumn(name = "type_id")
	private AccountTypeEntity type;

	public AccountEntity() {
	}

	public AccountTypeEntity getType() {
		return type;
	}

	public void setType(AccountTypeEntity type) {
		this.type = type;
	}

	public Integer getInterest() {
		return interest;
	}

	public void setInterest(Integer interest) {
		this.interest = interest;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	@Override
	public String toString() {
		return super.toString();
	}
}
