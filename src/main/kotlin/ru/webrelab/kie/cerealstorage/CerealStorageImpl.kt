package ru.webrelab.kie.cerealstorage

import org.slf4j.LoggerFactory

class CerealStorageImpl(

    override val containerCapacity: Float,
    override val storageCapacity: Float
) : CerealStorage {
    private val logger = LoggerFactory.getLogger(CerealStorageImpl::class.java)

    init {
        require(containerCapacity >= 0) {
            logger.error("Попытка создать хранилище с отрицательной ёмкостью контейнера: $containerCapacity")
            "Ёмкость контейнера не может быть отрицательной"
        }
        require(storageCapacity >= containerCapacity) {
            logger.error("Попытка создать хранилище с недопустимой общей ёмкостью: $storageCapacity (контейнер: $containerCapacity)")
            "Ёмкость хранилища не должна быть меньше ёмкости одного контейнера"
        }
        logger.info("Создано хранилище с ёмкостью контейнера $containerCapacity и общей ёмкостью $storageCapacity")
    }

    private val storage = mutableMapOf<Cereal, Float>()

    override fun addCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0) {
            logger.error("Попытка добавить отрицательное количество крупы: $amount")
            "Количество не может быть отрицательным"
        }

        val currentAmount = storage.getOrDefault(cereal, 0f)
        if (currentAmount == 0f && storage.size * containerCapacity >= storageCapacity) {
            logger.error("Невозможно добавить новый контейнер: хранилище заполнено")
            throw IllegalStateException("Нет места для нового контейнера")
        }

        val availableSpace = containerCapacity - currentAmount
        val toStore = minOf(amount, availableSpace)
        storage[cereal] = currentAmount + toStore

        val excess = amount - toStore
        logger.info("Добавлено $toStore единиц $cereal, излишек: $excess")
        return excess
    }

    override fun getCereal(cereal: Cereal, amount: Float): Float {
        require(amount >= 0) {
            logger.error("Попытка получить отрицательное количество крупы: $amount")
            "Количество не может быть отрицательным"
        }

        val currentAmount = storage.getOrDefault(cereal, 0f)
        val toReturn = minOf(amount, currentAmount)

        if (currentAmount > 0f) {
            storage[cereal] = currentAmount - toReturn
            logger.info("Извлечено $toReturn единиц $cereal, осталось: ${currentAmount - toReturn}")
        } else {
            logger.warn("Попытка получить крупу $cereal из пустого контейнера")
        }

        return toReturn
    }

    override fun removeContainer(cereal: Cereal): Boolean {
        val amount = storage.getOrDefault(cereal, 0f)
        return if (amount == 0f) {
            storage.remove(cereal)
            logger.info("Удален пустой контейнер для $cereal")
            true
        } else {
            logger.warn("Попытка удалить непустой контейнер для $cereal (содержит $amount)")
            false
        }
    }

    override fun getAmount(cereal: Cereal): Float {
        val amount = storage.getOrDefault(cereal, 0f)
        logger.debug("Запрошено количество $cereal: $amount")
        return amount
    }

    override fun getSpace(cereal: Cereal): Float {
        val currentAmount = storage.getOrDefault(cereal, 0f)
        val space = if (currentAmount > 0f) {
            containerCapacity - currentAmount
        } else if (storage.size * containerCapacity < storageCapacity) {
            containerCapacity
        } else {
            0f
        }
        logger.debug("Запрошено свободное место для $cereal: $space")
        return space
    }

    override fun toString(): String {
        return storage.entries.joinToString("\n") { (cereal, amount) ->
            "$cereal: $amount"
        }.also { logger.debug("Запрошено текстовое представление хранилища") }
    }
}
