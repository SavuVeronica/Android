package com.example.lab1.controller;

import com.example.lab1.model.Drug;
import com.example.lab1.repository.Repository;

import java.util.List;

public class Controller {
    private Repository repository;
    private static int id;

    public Controller(Repository repository)
    {
        this.repository = repository;
        id = 0;
    }

    public int add(String name, String composition, String use, int quantity, boolean recipe, double price) throws Exception
    {
        // Add drug + back to drugs list + refresh drugs list
        Drug drug = new Drug(id, quantity,name, composition,use, recipe, price);
        id +=1;
        repository.add(drug);
        return id-1;
    }

    public int getLastId()
    {
        return id-1;
    }

    public Drug find(int id)
    {
        return repository.find(id);
    }

    public void delete(int id) throws Exception
    {
        repository.remove(new Drug(id));
    }

    public void update(int id, int quantity) throws Exception
    {
        //repository.update(new Drug(id,quantity));
    }

    public List<Drug> getAllDrugs()
    {
        return repository.getAllDrugs();
    }

    public Repository getRepository()
    {
        return repository;
    }
}
