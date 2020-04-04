package dev.hyein.lecture.restapisample.events;




import junitparams.JUnitParamsRunner;
import junitparams.NamedParameters;
import junitparams.Parameters;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.assertj.core.api.Assertions.*;
@RunWith(JUnitParamsRunner.class)
public class EventTest {
    @Test
    public void builder(){
        Event event = Event.builder().build();
        assertThat(event).isNotNull();
    }

    @Test
    public void javaBeanSpec(){
        //Given
        String name = "event";
        String desc = "desc";

        //When
        Event event = new Event();
        event.setName(name);
        event.setDescription(desc);

        //Then
        assertThat(event.getName()).isEqualTo(name);
        assertThat(event.getDescription()).isEqualTo(desc);
    }

    @Test
    @Parameters(named = "hahaha")
    public void isFreeTest(int basePrice, int maxPrice, boolean isFree){
        //given
        Event event = Event.builder()
                    .basePrice(basePrice)
                    .maxPrice(maxPrice)
                    .build();
        //when
        event.update();

        //then
        assertThat(event.isFree()).isEqualTo(isFree);

    }

    //"parametersFor": 컨벤션. 파라미터 반환하는 메소드명이 "parametersFor" + 테스트 메소드 명이면 자동으로 바인딩해줌.
    @NamedParameters("hahaha")
    private Object[] forIsFreeTest(){
        return new Object[]{
                new Object[] {0, 0, true},
                new Object[] {0, 100, false},
                new Object[] {100, 0, false},
        };
    }

    @Test
    @Parameters
    public void testIsOffline(String location, boolean isOffline){
        //given
        Event event = Event.builder()
                .location(location)
                .build();
        //when
        event.update();

        //then
        assertThat(event.isOffline()).isEqualTo(isOffline);

    }

    public Object[] parametersForTestIsOffline(){
        return new Object[]{
                new Object[] {"강남역", true},
                new Object[] {"", false},
                new Object[] {null, false}
        };
    }
}