package ru.webrelab.kie.cerealstorage

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach
import ru.webrelab.kie.cerealstorage.Cereal.*

class CerealStorageImplTest {

    private lateinit var storage: CerealStorageImpl

    @BeforeEach
    fun setup() {
        storage = CerealStorageImpl(10f, 20f)
    }

    @Test
    fun `should throw if containerCapacity is negative`() {
        assertThatThrownBy {
            CerealStorageImpl(-4f, 10f)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should throw if storageCapacity is less than containerCapacity`() {
        assertThatThrownBy {
            CerealStorageImpl(10f, 5f)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should add cereal and return excess`() {
        assertThat(storage.addCereal(BUCKWHEAT, 15f))
            .describedAs("Ожидался излишек 5f, но получено %s", storage.addCereal(BUCKWHEAT, 15f))
            .isEqualTo(5f)

        assertThat(storage.getAmount(BUCKWHEAT))
            .describedAs("Ожидалось 10f гречки, но получено %s", storage.getAmount(BUCKWHEAT))
            .isEqualTo(10f)
    }

    @Test
    fun `should throw when adding negative amount`() {
        assertThatThrownBy {
            storage.addCereal(RICE, -5f)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should throw when storage is full for new cereal`() {
        storage.addCereal(BUCKWHEAT, 10f)
        storage.addCereal(RICE, 10f)

        assertThatThrownBy {
            storage.addCereal(MILLET, 5f)
        }.isInstanceOf(IllegalStateException::class.java)
    }

    @Test
    fun `should get cereal and return actual amount`() {
        storage.addCereal(RICE, 8f)
        val retrievedAmount = storage.getCereal(RICE, 5f)

        assertThat(retrievedAmount)
            .describedAs("Ожидалось получить 5f риса, но получено %s", retrievedAmount)
            .isEqualTo(5f)

        val remainingAmount = storage.getAmount(RICE)
        assertThat(remainingAmount)
            .describedAs("Ожидалось остаточное количество риса 3f, но получено %s", remainingAmount)
            .isEqualTo(3f)
    }

    @Test
    fun `should throw when getting negative amount`() {
        assertThatThrownBy {
            storage.getCereal(RICE, -5f)
        }.isInstanceOf(IllegalArgumentException::class.java)
    }

    @Test
    fun `should return zero when getting from empty container`() {
        assertThat(storage.getCereal(MILLET, 5f))
            .describedAs("Ожидалось получить 0f из пустого контейнера проса, но получено %s",
                storage.getCereal(MILLET, 5f))
            .isEqualTo(0f)
    }

    @Test
    fun `should remove empty container`() {
        storage.addCereal(RICE, 5f)
        storage.getCereal(RICE, 5f)

        assertThat(storage.removeContainer(RICE))
            .describedAs("Ожидалось успешное удаление пустого контейнера риса, но произошла ошибка")
            .isTrue()
    }

    @Test
    fun `should not remove non-empty container`() {
        storage.addCereal(RICE, 5f)

        assertThat(storage.removeContainer(RICE))
            .describedAs("Ожидалось, что непустой контейнер риса не будет удален, но он был удален")
            .isFalse()
    }

    @Test
    fun `should return correct amount`() {
        storage.addCereal(BUCKWHEAT, 7f)

        assertThat(storage.getAmount(BUCKWHEAT))
            .describedAs("Ожидалось 7f гречки, но получено %s", storage.getAmount(BUCKWHEAT))
            .isEqualTo(7f)

        assertThat(storage.getAmount(RICE))
            .describedAs("Ожидалось 0f риса, но получено %s", storage.getAmount(RICE))
            .isEqualTo(0f)
    }

    @Test
    fun `should return correct space`() {
        storage.addCereal(RICE, 6f)

        assertThat(storage.getSpace(RICE))
            .describedAs("Ожидалось 4f свободного места для риса, но получено %s", storage.getSpace(RICE))
            .isEqualTo(4f)

        assertThat(storage.getSpace(MILLET))
            .describedAs("Ожидалось 10f свободного места для проса, но получено %s", storage.getSpace(MILLET))
            .isEqualTo(10f)

        storage.addCereal(BUCKWHEAT, 10f)
        assertThat(storage.getSpace(MILLET))
            .describedAs("Ожидалось 0f свободного места для проса, но получено %s", storage.getSpace(MILLET))
            .isEqualTo(0f)
    }

    @Test
    fun `should correctly format toString`() {
        storage.addCereal(RICE, 5f)
        storage.addCereal(BUCKWHEAT, 3f)

        assertThat(storage.toString())
            .describedAs("Ожидалось, что toString вернет 'RICE: 5.0\nBUCKWHEAT: 3.0', но получено %s",
                storage.toString())
            .isEqualTo("RICE: 5.0\nBUCKWHEAT: 3.0")
    }
}
