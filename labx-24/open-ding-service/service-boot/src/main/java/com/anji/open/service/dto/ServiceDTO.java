package com.anji.open.service.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @ClassName ServiceDTO
 * @Description: TODO
 * @Author dingkaiqiang
 * @Date 2020-05-12
 * @Version V1.0
 **/
@Data
@Builder
public class ServiceDTO {


    private String name;

    private int age;

    private int serviceId;

    private String serviceBody;
}
