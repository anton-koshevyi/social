package com.social.backend.dto;

/**
 * Base interface for mapping of response. By default, maps entity to dto.
 * Contains methods to responding based on authority of user who made request.
 *
 * @param <T> Source (entity) type
 * @param <R> Target (dto) type
 */
public interface ResponseMapper<T, R> {
    /**
     * Return public entity data, available for all users or anonymous.
     */
    R map(T source);
    
    /**
     * Return data which not available for specific groups of users
     * (e.g. available only for owner (and for administration)).
     */
    default R mapHidden(T source) {
        return this.map(source);
    }
    
    /**
     * Return regular data including possible sensitive data.
     * Invoked on request from user with administrative authority.
     */
    default R mapExtended(T source) {
        return this.map(source);
    }
}
