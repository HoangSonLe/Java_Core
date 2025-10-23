package com.example.core.common.interfaces;

public interface IMapper<E, D> {
  D toDto(E entity);

  E toEntity(D dto);
}
