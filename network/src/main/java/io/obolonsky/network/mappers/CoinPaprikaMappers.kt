package io.obolonsky.network.mappers

import io.obolonsky.core.di.data.coinpaprika.CoinPaprika
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.responses.coinpaprika.CoinDetailsResponse
import io.obolonsky.network.responses.coinpaprika.CoinFeedItemResponse
import io.obolonsky.network.utils.fieldShouldNotBeNull

class CoinDetailsResponseToCoinPaprikaMapper : Mapper<CoinDetailsResponse, CoinPaprika> {

    override fun map(input: CoinDetailsResponse): CoinPaprika {
        return CoinPaprika(
            id = input.id ?: "id".fieldShouldNotBeNull(),
            isActive = input.isActive ?: "isActive".fieldShouldNotBeNull(),
            isNew = input.isNew,
            name = input.name ?: "name".fieldShouldNotBeNull(),
            rank = input.rank ?: "rank".fieldShouldNotBeNull(),
            symbol = input.symbol,
            type = input.type,
        )
    }
}

class ListCoinFeedItemResponseToListCoinPaprikaMapper :
    Mapper<List<CoinFeedItemResponse>, List<CoinPaprika>> {

    override fun map(input: List<CoinFeedItemResponse>): List<CoinPaprika> {
        val itemMapper = CoinFeedItemResponseToCoinPaprikaMapper()
        return input.map { itemMapper.map(it) }
    }

    class CoinFeedItemResponseToCoinPaprikaMapper : Mapper<CoinFeedItemResponse, CoinPaprika> {

        override fun map(input: CoinFeedItemResponse): CoinPaprika {
            return CoinPaprika(
                id = input.id ?: "id".fieldShouldNotBeNull(),
                isActive = input.isActive ?: "isActive".fieldShouldNotBeNull(),
                isNew = input.isNew,
                name = input.name ?: "name".fieldShouldNotBeNull(),
                rank = input.rank ?: "rank".fieldShouldNotBeNull(),
                symbol = input.symbol,
                type = input.type,
            )
        }
    }
}
