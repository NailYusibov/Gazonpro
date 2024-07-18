package com.gitlab.model;

import lombok.Data;

import javax.xml.bind.annotation.*;
import java.util.List;

@Data
@XmlRootElement(name = "ValCurs")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValCurs {

    @XmlAttribute(name = "ID")
    private String id;

    @XmlAttribute(name = "DateRange1")
    private String dateRange1;

    @XmlAttribute(name = "DateRange2")
    private String dateRange2;

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "Record")
    private List<Record> record;
}
