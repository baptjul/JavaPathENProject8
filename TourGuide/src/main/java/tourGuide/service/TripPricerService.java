package tourGuide.service;

import org.javamoney.moneta.Money;
import org.springframework.stereotype.Service;
import tourGuide.user.User;
import tripPricer.Provider;
import tripPricer.TripPricer;

import javax.money.CurrencyUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class TripPricerService {

    private final TripPricer tripPricer;
    private static final String tripPricerApiKey = "test-server-api-key";

    public TripPricerService(TripPricer tripPricer) {
        this.tripPricer = tripPricer;
    }

    public List<Provider> getPrice(User user, UUID userId, int adults, int children, int nightsStay, int rewardsPoints ) {
        CurrencyUnit userCurrency = user.getUserPreferences().getCurrency();
        Money lowerPricePoint = user.getUserPreferences().getLowerPricePoint();
        Money highPricePoint = user.getUserPreferences().getHighPricePoint();
        int ticketQuantity = user.getUserPreferences().getTicketQuantity();

        List<Provider> providers =  tripPricer.getPrice(
                tripPricerApiKey,
                userId,
                adults,
                children,
                nightsStay,
                rewardsPoints
        );

        // check quantity
        List<Provider> updatedProviders = new ArrayList<>();
        for (Provider provider : providers) {
            double updatedPrice = provider.price * ticketQuantity;
            Provider updatedProvider = new Provider(provider.tripId, provider.name, updatedPrice);
            updatedProviders.add(updatedProvider);
        }

        // check price range
        return updatedProviders.stream()
                .filter(provider -> {
                    Money priceInUserCurrency = Money.of(provider.price, userCurrency);
                    return priceInUserCurrency.isGreaterThanOrEqualTo(lowerPricePoint) &&
                            priceInUserCurrency.isLessThanOrEqualTo(highPricePoint);
                })
                .toList();
    }
}
