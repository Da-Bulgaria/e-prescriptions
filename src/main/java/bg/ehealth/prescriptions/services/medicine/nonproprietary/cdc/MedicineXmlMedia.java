package bg.ehealth.prescriptions.services.medicine.nonproprietary.cdc;

import bg.ehealth.prescriptions.persistence.model.NonProprietaryMedicineName;
import bg.ehealth.prescriptions.services.medicine.nonproprietary.MedicineNamesMedia;
import com.google.common.base.Strings;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

//TODO rework with ContentHandlers
@Service
public class MedicineXmlMedia implements MedicineNamesMedia {

    private static Logger LOGGER = LoggerFactory.getLogger(MedicineXmlMedia.class);

    private final XMLInputFactory xmlInputFactory;

    public MedicineXmlMedia() {
        this.xmlInputFactory = XMLInputFactory.newInstance();
    }

    @Override
    public Set<NonProprietaryMedicineName> medicineNames(InputStream inputStream) {
        XMLEventReader xmlEventReader = xmlEventReader(inputStream);
        String mainTitle = "";
        boolean inMainTerm = false;
        boolean inTerm;
        Map<Integer, List<String>> levels = new HashMap<>();
        Set<String> titles = new HashSet<>();
        while (xmlEventReader.hasNext()) {
            XMLEvent currentEvent = nextEvent(xmlEventReader);
            if (isStartElement(currentEvent, "mainTerm")) {
                inMainTerm = true;
                mainTitle = mainTermTitle(xmlEventReader);
            }
            int currLevel = -1;
            int prevLevel = -1;
            while (inMainTerm) {
                XMLEvent xmlEvent = (XMLEvent) xmlEventReader.next();
                if (isStartElement(xmlEvent, "term")) {
                    inTerm = true;
                    while (inTerm) {
                        if (isStartElement(xmlEvent, "term")) {
                            currLevel = levelAttribute(xmlEvent);
                            if (prevLevel > currLevel) {
                                while (prevLevel > currLevel) {
                                    rollUpLevels(levels, prevLevel);
                                    prevLevel--;
                                }
                            }
                            prevLevel = currLevel - 1;
                        }
                        xmlEvent = (XMLEvent) xmlEventReader.next();
                        if (isStartElement(xmlEvent, "title")) {
                            String termTitle = title((xmlEventReader));
                            levels.merge(currLevel, Lists.newArrayList(termTitle), (prevTitles, newTitles) -> {
                                prevTitles.addAll(newTitles);
                                return prevTitles;
                            });
                        }

                        if (isEndElement(xmlEvent, "term")) {
                            inTerm = false;
                            xmlEvent = nextTag(xmlEventReader);
                            if (isEndElement(xmlEvent, "term")) {
                                if (currLevel > prevLevel && currLevel != 1) {
                                    rollUpLevels(levels, currLevel);
                                }
                            } else if (isStartElement(xmlEvent, "term")) {
                                inTerm = true;
                            }
                        }
                    }
                }

                if (isEndElement(xmlEvent, "mainTerm")) {
                    inMainTerm = false;
                }

                if (!inMainTerm) {
                    if (levels.isEmpty()) {
                        titles.add(mainTitle);
                    } else {
                        titles.addAll(mainTitles(levels, mainTitle, 5));
                        levels.clear();
                    }
                }
            }
        }

        try {
            xmlEventReader.close();
        } catch (XMLStreamException e) {
            LOGGER.error("Unable to close XML event reader!", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                LOGGER.error("Unable to close input stream!", e);
            }
        }

        return titles.stream().map(NonProprietaryMedicineName::new).collect(Collectors.toSet());
    }

    private XMLEvent nextEvent(XMLEventReader xmlEventReader) {
        XMLEvent result;
        try {
            result = xmlEventReader.nextEvent();
        } catch (XMLStreamException e) {
            LOGGER.error("Unable to get next event!", e);
            throw new MedicineXmlMediaException("Unable to read cdc medicine names!", e);
        } finally {
            close(xmlEventReader);
        }

        return result;
    }

    private XMLEvent nextTag(XMLEventReader xmlEventReader) {
        XMLEvent result;
        try {
            result = xmlEventReader.nextTag();
        } catch (XMLStreamException e) {
            LOGGER.error("Unable to get next tag!", e);
            throw new MedicineXmlMediaException("Unable to read cdc medicine names!", e);
        } finally {
            close(xmlEventReader);
        }

        return result;
    }

    private void close(XMLEventReader xmlEventReader) {
        try {
            xmlEventReader.close();
        } catch (XMLStreamException e) {
            LOGGER.error("Unable to close xmlEventReader!", e);
        }
    }

    private XMLEventReader xmlEventReader(InputStream inputStream) {
        XMLEventReader reader;
        try {
            reader = xmlInputFactory.createXMLEventReader(inputStream, StandardCharsets.UTF_8.name());
            xmlInputFactory.createFilteredReader(reader,
                    event -> !isStartElement(event, "cell")
                            || !isEndElement(event, "cell")
                            || !event.asCharacters().isWhiteSpace()
            );
        } catch (XMLStreamException e) {
            LOGGER.error("Unable to create XMLEventReader!", e);
            throw new MedicineXmlMediaException("Unable to read cdc medicine names!", e);
        }

        return reader;
    }

    private String mainTermTitle(XMLEventReader xmlEventReader) {
        String mainTitle = "";
        if (isStartElement(nextTag(xmlEventReader), "title")) {
            mainTitle = title(xmlEventReader);
        }

        return mainTitle;
    }

    private void rollUpLevels(Map<Integer, List<String>> levels, int rollUpLevel) {
        List<String> prevLevelTermTitles = levels.get(rollUpLevel - 1);
        String last = Iterables.getLast(prevLevelTermTitles);
        int indexOfLast = prevLevelTermTitles.size() - 1;
        prevLevelTermTitles.remove(indexOfLast);
        List<String> currLevelTermTitles = levels.get(rollUpLevel);
        currLevelTermTitles.forEach(currTermTitle ->
                prevLevelTermTitles.add(last + " - " + currTermTitle));
        levels.remove(rollUpLevel);
    }

    private List<String> mainTitles(Map<Integer, List<String>> levels, String mainTitle, int maxLevel) {
        List<String> oldTermTitles = new ArrayList<>();
        for (Map.Entry<Integer, List<String>> entry : levels.entrySet()) {
            List<String> termTitles = entry.getValue();
            if (entry.getKey() == 1) {
                oldTermTitles.addAll(termTitles);
            } else {
                oldTermTitles = oldTermTitles.stream()
                        .flatMap(oldTitle -> termTitles.stream().map(termTitle -> oldTitle + " - " + termTitle))
                        .collect(Collectors.toList());
            }
        }

        return oldTermTitles.stream()
                .map(termTitle -> mainTitle.toLowerCase().trim() + " - " + termTitle.toLowerCase().trim())
                .collect(Collectors.toList());
    }

    private int levelAttribute(XMLEvent xmlEvent) {
        return Integer.parseInt(xmlEvent.asStartElement()
                .getAttributeByName(new QName("level")).getValue());
    }

    private String title(XMLEventReader xmlEventReader) {
        String mainTitle;
        String title = "";
        String nemod = "";
        StringBuilder sb = new StringBuilder();
        try {
            charactersAhead(sb, xmlEventReader);
            XMLEvent next = xmlEventReader.nextTag();
            if (isEndElement(next, "title")) {
                title = sb.toString();
                sb.setLength(0);
            } else if (isStartElement(next, "nemod")) {
                title = sb.toString();
                sb.setLength(0);
                charactersAhead(sb, xmlEventReader);

                XMLEvent next2 = xmlEventReader.nextTag();
                if (isEndElement(next2, "nemod")) {
                    nemod = sb.toString();
                    sb.setLength(0);
                }

                while (xmlEventReader.peek().isCharacters()) {
                    sb.append(xmlEventReader.nextEvent().asCharacters().getData());
                }
            }
        } catch (XMLStreamException ex) {
            LOGGER.error("Unable to parse document", ex);
        }

        if (Strings.isNullOrEmpty(nemod)) {
            mainTitle = title.trim() + sb.toString().trim();
        } else {
            mainTitle = title.trim() + " " + nemod.trim() + " " + sb.toString().trim();
        }

        sb.setLength(0);

        return mainTitle.trim();
    }

    private void charactersAhead(StringBuilder sb, XMLEventReader xmlEventReader) throws XMLStreamException {
        while (xmlEventReader.peek().isCharacters()) {
            XMLEvent nextEvent = xmlEventReader.nextEvent();
            sb.append(nextEvent.asCharacters().getData());
        }
    }

    private boolean isStartElement(XMLEvent event, String name) {
        return event.isStartElement() && event.asStartElement().getName().getLocalPart().equals(name);
    }

    private boolean isEndElement(XMLEvent event, String name) {
        return event.isEndElement() && event.asEndElement().getName().getLocalPart().equals(name);
    }

}
