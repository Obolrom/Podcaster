package io.obolonsky.network.mappers

import io.obolonsky.core.di.data.coinpaprika.CoinPaprika
import io.obolonsky.core.di.utils.Mapper
import io.obolonsky.network.responses.coinpaprika.CoinDetailsResponse
import io.obolonsky.network.responses.coinpaprika.CoinFeedItemResponse

class CoinDetailsResponseToCoinPaprikaMapper : Mapper<CoinDetailsResponse, CoinPaprika> {

    override fun map(input: CoinDetailsResponse): CoinPaprika {
        return CoinPaprika(
            id = requireNotNull(input.id),
            isActive = requireNotNull(input.isActive),
            isNew = input.isNew,
            name = requireNotNull(input.name),
            rank = requireNotNull(input.rank),
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
                id = requireNotNull(input.id),
                isActive = requireNotNull(input.isActive),
                isNew = input.isNew,
                name = requireNotNull(input.name),
                rank = requireNotNull(input.rank),
                symbol = input.symbol,
                type = input.type,
            )
        }
    }
}
