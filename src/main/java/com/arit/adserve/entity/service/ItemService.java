package com.arit.adserve.entity.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.PersistenceException;

import com.arit.adserve.comm.SpringUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.arit.adserve.entity.Item;
import com.arit.adserve.entity.repository.ItemRepository;

@Service
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public Item findById(String id) {
        return itemRepository.findById(id).orElse(null);
    }


    public List<Item> findAll(int pageNumber, int rowPerPage) {
        List<Item> items = new ArrayList<>();
        PageRequest sortedByLastUpdateDesc = PageRequest.of(pageNumber - 1, rowPerPage,
                Sort.by("title").descending());
        itemRepository.findAll(sortedByLastUpdateDesc).forEach(items::add);
        return items;
    }

    public Item save(Item item) {
        SpringUtil.validateInput(item);
        return itemRepository.save(item);
    }

    public void update(Item item) {
        SpringUtil.validateInput(item);
        itemRepository.save(item);
    }

    public void deleteById(String id) {
        if (!existsById(id)) {
            throw new PersistenceException("Cannot find Item with id: " + id);
        } else {
            itemRepository.deleteById(id);
        }
    }

    public Long count() {
        return itemRepository.count();
    }

    private boolean existsById(String id) {
        return itemRepository.existsById(id);
    }

    public List<Item> getItemsFromProviderBefore(Date dateLimitForItems, String providerName, int maxItemsForUpdate) {
        PageRequest pageableLimit = PageRequest.of(0, maxItemsForUpdate);
        return itemRepository.getItemsFromProviderBefore(dateLimitForItems, providerName, pageableLimit);
    }

    public Item findByProviderId(String providerItemId, String providerName) {
        return itemRepository.findByProviderId(providerItemId, providerName);
    }

    public Iterable<Item> findAllByProviderIds(List<String> itemEbayIds, String providerName) {
        return itemRepository.findAllByProviderIds(itemEbayIds, providerName);
    }

    public void updateAll(Iterable<Item> updatedItems) {
        itemRepository.saveAll(updatedItems);
    }

    public boolean hasImage(String providerItemId, String providerName) {
        Item item = itemRepository.findByProviderId(providerItemId, providerName);
        return item != null && StringUtils.isNotEmpty(item.getImage64BaseStr());
    }

    public long countNotDeleted() {
        Item item = new Item();
        item.setDeleted(false);
        Example<Item> example = Example.of(item);
        return itemRepository.count(example);
    }

    public long countItemsUpdatedAfter(Date dateLimitForItems, String providerName) {
        return itemRepository.countItemsUpdatedAfter(dateLimitForItems, providerName);
    }
}
