package me.miki.shindo.libs.spotify.requests.data;

import me.miki.shindo.libs.spotify.model_objects.specification.Paging;

public abstract class AbstractDataPagingRequest<T> extends AbstractDataRequest<T> {
    protected AbstractDataPagingRequest(final AbstractDataRequest.Builder<T, ?> builder) {
        super(builder);
    }

    public static abstract class Builder<T, BT extends Builder<T, ?>>
            extends AbstractDataRequest.Builder<Paging<T>, BT>
            implements IPagingRequestBuilder<T, BT> {
        protected Builder(String accessToken) {
            super(accessToken);

            assert (!accessToken.isEmpty());

            setHeader("Authorization", "Bearer " + accessToken);
        }
    }
}
