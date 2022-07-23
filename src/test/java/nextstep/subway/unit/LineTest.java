package nextstep.subway.unit;

import nextstep.subway.domain.Line;
import nextstep.subway.domain.Section;
import nextstep.subway.domain.Station;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LineTest {
    @Test
    void addSection() {
        //Given
        Station magok = new Station(1L, "마곡역");
        Station balsan = new Station(2L, "발산역");
        Line line = new Line("5호선", "purple");

        //When
        Section section = new Section(line, magok, balsan, 10);
        line.addSection(section);

        //Then
        assertThat(line.getSections()).hasSize(1);
        assertThat(line.getSections()).containsExactly(section);
    }

    @Test
    void addSectionBaseOnUpstation() {
        //Given
        Station magok = new Station(1L, "마곡역");
        Station woojangsan = new Station(2L, "우장산역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");

        //When
        Section section1 = new Section(line, magok, woojangsan, 10);
        line.addSection(section1);

        Section section2 = new Section(line, magok, balsan, 3);
        line.addSection(section2);

        List<Section> sections = line.getSections();

        assertThat(sections).hasSize(2);
        assertThat(sections.get(0).getUpStation()).isEqualTo(section2.getUpStation());
        assertThat(sections.get(0).getDownStation()).isEqualTo(section2.getDownStation());
        assertThat(sections.get(1).getUpStation()).isEqualTo(section2.getDownStation());
        assertThat(sections.get(1).getDownStation()).isEqualTo(section1.getDownStation());
    }

    @Test
    void addSectionFirst(){
        //Given
        Station magok = new Station(1L, "마곡역");
        Station woojangsan = new Station(2L, "우장산역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");
        //When
        Section section1 = new Section(line, balsan, woojangsan, 10);
        line.addSection(section1);

        Section section2 = new Section(line, magok, balsan, 3);
        line.addSection(section2);

        List<Section> sections = line.getSections();

        assertThat(sections).hasSize(2);
        assertThat(sections.get(0).getUpStation()).isEqualTo(section2.getUpStation());
        assertThat(sections.get(0).getDownStation()).isEqualTo(section2.getDownStation());
        assertThat(sections.get(1).getUpStation()).isEqualTo(section1.getUpStation());
        assertThat(sections.get(1).getDownStation()).isEqualTo(section1.getDownStation());
    }

    @Test
    void addSectionEnd(){
        //Given
        Station magok = new Station(1L, "마곡역");
        Station woojangsan = new Station(2L, "우장산역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");
        //When
        Section section1 = new Section(line, magok, balsan, 3);
        line.addSection(section1);

        Section section2 = new Section(line, balsan, woojangsan, 3);
        line.addSection(section2);

        List<Section> sections = line.getSections();

        assertThat(sections).hasSize(2);
        assertThat(sections.get(0).getUpStation()).isEqualTo(section1.getUpStation());
        assertThat(sections.get(0).getDownStation()).isEqualTo(section1.getDownStation());
        assertThat(sections.get(1).getUpStation()).isEqualTo(section2.getUpStation());
        assertThat(sections.get(1).getDownStation()).isEqualTo(section2.getDownStation());
    }

    @Test
    void hasNoStationException(){
        //Given
        Station magok = new Station(1L, "마곡역");
        Station balsan = new Station(3L, "발산역");
        Station woojangsan = new Station(4L, "우장산역");
        Station gimpo = new Station(4L, "김포공항역");
        Line line = new Line("5호선", "purple");
        //When
        Section section1 = new Section(line, magok, balsan, 3);
        line.addSection(section1);

        Section section2 = new Section(line, woojangsan, gimpo, 2);
        assertThatThrownBy(() ->line.addSection(section2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void allRegistException(){
        //Given
        Station magok = new Station(1L, "마곡역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");
        //When
        Section section1 = new Section(line, magok, balsan, 3);
        line.addSection(section1);

        Section section2 = new Section(line, balsan, balsan, 2);
        assertThatThrownBy(() ->line.addSection(section2))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void distanceException(){
        //Given
        Station magok = new Station(1L, "마곡역");
        Station woojangsan = new Station(2L, "우장산역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");
        //When
        Section section1 = new Section(line, magok, woojangsan, 3);
        line.addSection(section1);

        Section section2 = new Section(line, balsan, woojangsan, 10);
        assertThatThrownBy(() ->line.addSection(section2))
                .isInstanceOf(IllegalArgumentException.class);

    }



    @Test
    void getStations() {
        //Given
        Station magok = new Station(1L, "마곡역");
        Station balsan = new Station(2L, "발산역");
        Line line = new Line("5호선", "purple");
        Section section = new Section(line, magok, balsan, 10);
        line.addSection(section);

        //When
        List<Station> stations = line.getStations();

        //Then
        assertThat(stations).containsExactly(magok, balsan);
    }

    @Test
    void removeSection() {
        //Given
        Station songjeong = new Station(1L, "송정역");
        Station magok = new Station(2L, "마곡역");
        Station balsan = new Station(3L, "발산역");
        Line line = new Line("5호선", "purple");
        Section section1 = new Section(line, songjeong, magok, 7);
        Section section2 = new Section(line, magok, balsan, 10);
        line.addSection(section1);
        line.addSection(section2);

        //When
        line.removeSection(line.getSections().size() - 1);

        //Then
        assertThat(line.getSections()).hasSize(1);

    }
}
