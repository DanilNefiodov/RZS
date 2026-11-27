package com.ua.gamestore.service;

import com.ua.gamestore.domain.Game;
import com.ua.gamestore.exception.GameNotFoundException;
import com.ua.gamestore.repository.GameRepository;
import com.ua.gamestore.service.mapper.GameMapper;
import com.ua.gamestore.web.dto.GameCardDto;
import com.ua.gamestore.web.dto.GameDetailDto;
import com.ua.gamestore.web.dto.GameRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UNIT-тест сервісу: репозиторій замокано (mock), маппер справжній.
 * Spring не запускається — сервіс збирається через конструктор, тому тест швидкий
 * і перевіряє лише логіку GameServiceImpl.
 */
class GameServiceImplTest {

    private final GameRepository repository = mock(GameRepository.class);
    private final GameMapper mapper = new GameMapper();
    private final GameService service = new GameServiceImpl(repository, mapper);

    private Game sampleGame() {
        return new Game(1L, "S.T.A.L.K.E.R. 2: Heart of Chornobyl", "GSC Game World",
                new BigDecimal("1299.00"), 2024, "PC", "Шутер", 18,
                "/covers/stalker-2.jpg", "Опис");
    }

    private GameRequest sampleRequest() {
        return new GameRequest("Hollow Knight", "Team Cherry", new BigDecimal("349.00"),
                2017, "Nintendo Switch", "Метроїдванія", 7,
                "/covers/hollow-knight.jpg", "Опис");
    }

    // ---------- findAll ----------

    @Test
    void findAll_withoutGenre_returnsAllGamesAsCards() {
        when(repository.findAll()).thenReturn(List.of(sampleGame()));

        List<GameCardDto> cards = service.findAll(null);

        assertThat(cards).hasSize(1);
        assertThat(cards.get(0).title()).isEqualTo("S.T.A.L.K.E.R. 2: Heart of Chornobyl");
        assertThat(cards.get(0).platform()).isEqualTo("PC");
        verify(repository, never()).findByGenre(any());
    }

    @Test
    void findAll_withBlankGenre_isTreatedAsNoFilter() {
        when(repository.findAll()).thenReturn(List.of(sampleGame()));

        assertThat(service.findAll("  ")).hasSize(1);
        verify(repository).findAll();
        verify(repository, never()).findByGenre(any());
    }

    @Test
    void findAll_withGenre_usesFilter() {
        when(repository.findByGenre("Шутер")).thenReturn(List.of(sampleGame()));

        List<GameCardDto> cards = service.findAll("Шутер");

        assertThat(cards).hasSize(1);
        verify(repository).findByGenre("Шутер");
        verify(repository, never()).findAll();
    }

    // ---------- findById ----------

    @Test
    void findById_returnsDetails() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleGame()));

        GameDetailDto dto = service.findById(1L);

        assertThat(dto.id()).isEqualTo(1L);
        assertThat(dto.developer()).isEqualTo("GSC Game World");
        assertThat(dto.ageRating()).isEqualTo(18);
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.findById(999L))
                .isInstanceOf(GameNotFoundException.class)
                .hasMessageContaining("999");
    }

    // ---------- create ----------

    @Test
    void create_savesWithoutIdAndReturnsAssignedId() {
        when(repository.save(any(Game.class))).thenAnswer(invocation -> {
            Game g = invocation.getArgument(0);
            g.setId(6L);                       // так робить справжній репозиторій
            return g;
        });

        GameDetailDto created = service.create(sampleRequest());

        ArgumentCaptor<Game> saved = ArgumentCaptor.forClass(Game.class);
        verify(repository).save(saved.capture());
        assertThat(saved.getValue().getTitle()).isEqualTo("Hollow Knight");
        assertThat(created.id()).isEqualTo(6L);
        assertThat(created.title()).isEqualTo("Hollow Knight");
    }

    // ---------- update ----------

    @Test
    void update_existing_keepsIdAndSavesNewData() {
        when(repository.findById(1L)).thenReturn(Optional.of(sampleGame()));
        when(repository.save(any(Game.class))).thenAnswer(invocation -> invocation.getArgument(0));

        GameDetailDto updated = service.update(1L, sampleRequest());

        assertThat(updated.id()).isEqualTo(1L);
        assertThat(updated.title()).isEqualTo("Hollow Knight");
    }

    @Test
    void update_missing_throwsAndDoesNotSave() {
        when(repository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(999L, sampleRequest()))
                .isInstanceOf(GameNotFoundException.class);
        verify(repository, never()).save(any());
    }

    // ---------- delete ----------

    @Test
    void delete_existing_removesFromRepository() {
        when(repository.deleteById(1L)).thenReturn(true);

        service.delete(1L);

        verify(repository).deleteById(1L);
    }

    @Test
    void delete_missing_throws() {
        when(repository.deleteById(999L)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(999L))
                .isInstanceOf(GameNotFoundException.class);
    }
}
