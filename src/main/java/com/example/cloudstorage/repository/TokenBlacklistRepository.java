package com.example.cloudstorage.repository;

import com.example.cloudstorage.model.TokenBlacklist;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TokenBlacklistRepository extends CrudRepository<TokenBlacklist, String> {
}
