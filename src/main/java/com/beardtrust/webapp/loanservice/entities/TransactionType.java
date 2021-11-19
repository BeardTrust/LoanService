package com.beardtrust.webapp.loanservice.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Objects;
import java.util.Set;

/**
 * This class represents Transaction type.
 *
 * @author Matthew Crowell <Matthew.Crowell@Smoothstack.com>
 */
@Entity
@Table(name = "transaction_types")
public class TransactionType {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int typeId;
	@Column(unique = true)
	private String typeName;
	private String typeDescription;
	private String sourceDescription;
	private String targetDescription;
	@JsonIgnore
	@OneToMany(mappedBy = "transactionType")
	private Set<FinancialTransaction> transactions;

	/**
	 * Instantiates a new Transaction type.
	 */
	public TransactionType() {
	}

	/**
	 * Instantiates a new Transaction type.
	 *
	 * @param typeId            int the type id
	 * @param typeName          String the type name
	 * @param typeDescription   String the type description
	 * @param sourceDescription String the source description
	 * @param targetDescription String the target description
	 */
	public TransactionType(int typeId, String typeName, String typeDescription, String sourceDescription, String targetDescription) {
		this.typeId = typeId;
		this.typeName = typeName;
		this.typeDescription = typeDescription;
		this.sourceDescription = sourceDescription;
		this.targetDescription = targetDescription;
	}

	/**
	 * Getter for the type id.
	 *
	 * @return int the type id
	 */
	public int getTypeId() {
		return typeId;
	}

	/**
	 * Setter for the type id.
	 *
	 * @param typeId int the type id
	 */
	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}

	/**
	 * Getter for the type name.
	 *
	 * @return String the type name
	 */
	public String getTypeName() {
		return typeName;
	}

	/**
	 * Setter for the type name.
	 *
	 * @param typeName String the type name
	 */
	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	/**
	 * Getter for the type description.
	 *
	 * @return String the type description
	 */
	public String getTypeDescription() {
		return typeDescription;
	}

	/**
	 * Setter for the type description.
	 *
	 * @param typeDescription String the type description
	 */
	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}

	/**
	 * Getter for the source description.
	 *
	 * @return String the source description
	 */
	public String getSourceDescription() {
		return sourceDescription;
	}

	/**
	 * Setter for the source description.
	 *
	 * @param sourceDescription String the source description
	 */
	public void setSourceDescription(String sourceDescription) {
		this.sourceDescription = sourceDescription;
	}

	/**
	 * Getter for the target description.
	 *
	 * @return String the target description
	 */
	public String getTargetDescription() {
		return targetDescription;
	}

	/**
	 * Setter for the target description.
	 *
	 * @param targetDescription String the target description
	 */
	public void setTargetDescription(String targetDescription) {
		this.targetDescription = targetDescription;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		TransactionType that = (TransactionType) o;
		return typeId == that.typeId && Objects.equals(typeName, that.typeName) && Objects.equals(typeDescription, that.typeDescription) && Objects.equals(sourceDescription, that.sourceDescription) && Objects.equals(targetDescription, that.targetDescription);
	}

	@Override
	public int hashCode() {
		return Objects.hash(typeId, typeName, typeDescription, sourceDescription, targetDescription);
	}

	@Override
	public String toString() {
		return this.typeName;
	}
}
