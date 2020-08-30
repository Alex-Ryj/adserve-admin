package com.arit.adserve.entity.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;
import org.springframework.stereotype.Repository;

import com.arit.adserve.entity.Item;

import java.util.Date;
import java.util.List;

@Repository
public interface ItemRepository extends PagingAndSortingRepository<Item, String>, QueryByExampleExecutor<Item> {

    @Query("SELECT it From Item it where it.providerItemId = ?1 and it.providerName = ?2")
    Item findByProviderId(String providerItemId, String providerName);

    @Query("SELECT it From Item it where it.providerItemId in ?1 and it.providerName = ?2")
    List<Item> findAllByProviderIds(List<String> providerItemIds, String providerName);

    @Query("SELECT it From Item it where it.updatedOn <= ?1 and it.providerName = ?2")
    List<Item> getItemsFromProviderBefore(Date date, String providerName, Pageable pageable);

    @Query("SELECT count(*) From Item where updatedOn >= ?1 and providerName = ?2")
    long countItemsUpdatedAfter(Date dateLimitForItems, String providerName);
}
