package com.gitlab.model.ExchangeRateModel;

import lombok.Data;

import javax.xml.bind.annotation.*;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class Record {

    @XmlAttribute(name = "Date")
    private String date;

    @XmlAttribute(name = "Id")
    private String id;

    @XmlElement(name = "Nominal")
    private int nominal;

    @XmlElement(name = "Value")
    private String value;

    @XmlElement(name = "VunitRate")
    private String vunitRate;
}
