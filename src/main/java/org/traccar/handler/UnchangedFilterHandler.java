
package org.traccar.handler;

import org.traccar.config.Keys;
import org.traccar.helper.model.AttributeUtil;
import org.traccar.model.Position;
import org.traccar.session.cache.CacheManager;

import jakarta.inject.Inject;
import java.util.Objects;

public class UnchangedFilterHandler extends BasePositionHandler {
    private final CacheManager cacheManager;

    @Inject
    public UnchangedFilterHandler(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void onPosition(Position position, Callback callback) {
        Boolean enabled = AttributeUtil.lookup(cacheManager, Keys.FILTER_UNCHANGED, position.getDeviceId());
        if (Boolean.TRUE.equals(enabled)) {
            Position last = cacheManager.getPosition(position.getDeviceId());
            if (last != null && isUnchanged(position, last)) {
                callback.processed(true);
                return;
            }
        }
        callback.processed(false);
    }

    private boolean isUnchanged(Position position, Position last) {
        return position.getValid() == last.getValid()
                && position.getLatitude() == last.getLatitude()
                && position.getLongitude() == last.getLongitude()
                && position.getAltitude() == last.getAltitude()
                && position.getSpeed() == last.getSpeed()
                && position.getCourse() == last.getCourse()
                && position.getAccuracy() == last.getAccuracy()
                && Objects.equals(position.getAttributes(), last.getAttributes());
    }

}
