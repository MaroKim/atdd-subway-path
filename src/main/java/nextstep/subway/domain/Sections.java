package nextstep.subway.domain;


import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Embeddable
public class Sections {
    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private List<Section> sections = new ArrayList<>();

    public Sections() {
    }

    public void addSection(Section section) {
        final Optional<Section> sectionFromDownStation = this.getSectionFromDownStation(section.getDownStation());
        if (sectionFromDownStation.isPresent()) {
            if (sectionFromDownStation.get().getDistance() < section.getDistance()) {
                throw new IllegalArgumentException("노선 길이가 부족합니다");
            }
             sectionFromDownStation.get().updateDownStation(section.getUpStation());
        }

        this.sections.add(section);
    }

    public List<Station> getAllStations() {
        List<Station> result = new ArrayList<>();
        Section firstSection = this.findFirstSection()
                .orElseThrow(() -> new IllegalArgumentException("호선의 첫번째 노선을 찾을 수 없습니다."));

        result.add(firstSection.getUpStation());
        Station start = firstSection.getDownStation();
        result.add(start);

        while(true) {
            final Optional<Section> findSection = this.getSectionFromUpStation(start);
            if (!findSection.isPresent()) {
                break;
            }
            final Station downStation = findSection.get().getDownStation();
            result.add(downStation);
            start = downStation;
        }
        return result;
    }

    private Optional<Section> findFirstSection() {
        final List<Station> allUpStations = this.getAllUpStations();
        final List<Station> allDownStations = this.getAllDownStations();

        for (Station upStation : allUpStations) {
            if (!allDownStations.contains(upStation)) {
                return getSectionFromUpStation(upStation);
            }
        }
        return Optional.empty();

    }

    public void deleteSection(Station station) {
        final int lastIndex = this.sections.size() - 1;
        final Section lastSection = this.sections.get(lastIndex);
        if (!lastSection.isDownStation(station)) {
            throw new IllegalArgumentException();
        }

        this.sections.remove(lastIndex);
    }

    public boolean isEmpty() {
        return this.sections.isEmpty();
    }

    public int size() {
        return this.sections.size();
    }

    public Optional<Section> getSectionFromUpStation(Station upStation) {
        return sections.stream()
                .filter(section -> section.isUpStation(upStation))
                .findFirst();
    }

    public Optional<Section> getSectionFromDownStation(Station downStation) {
        return sections.stream()
                .filter(section -> section.isDownStation(downStation))
                .findFirst();
    }

    public boolean isFirst(Section section) {
        final Station upStation = section.getUpStation();

        if (getAllUpStations().contains(upStation) && !getAllDownStations().contains(upStation)) {
            return true;
        }
        return false;

    }

    public boolean isLast(Section section) {
        final Station downStation = section.getDownStation();

        if (!getAllUpStations().contains(downStation) && getAllDownStations().contains(downStation)) {
            return true;
        }
        return false;
    }


    private List<Station> getAllUpStations() {
        return this.sections.stream()
                .map(Section::getUpStation)
                .collect(Collectors.toList());
    }

    private List<Station> getAllDownStations() {
        return this.sections.stream()
                .map(Section::getDownStation)
                .collect(Collectors.toList());
    }
}
