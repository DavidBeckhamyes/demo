package com.example.entity;

public class Product {
	private String name;
	private double price;
	private int inStock;

	public String getName() {
		return name;
	}

	public Product() {
	}

	public Product(String name, double price, int inStock) {
		this.name = name;
		this.price = price;
		this.inStock = inStock;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getInStock() {
		return inStock;
	}

	public void setInStock(int inStock) {
		this.inStock = inStock;
	}

}
