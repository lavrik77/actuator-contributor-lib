package ru.globaltruck.actuator.configuration.loader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Slf4j
public class LibPropertiesLoader implements EnvironmentPostProcessor {

  private final YamlPropertySourceLoader propertySourceLoader = new YamlPropertySourceLoader();

  private static final String DEFAULT_LIB_PROPERTIES = "defaultLibProperties";
  private static final String LIB_YML = "lib.yml";

  @Override
  public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
    try {
      propertySourceLoader.load(DEFAULT_LIB_PROPERTIES, new ClassPathResource(LIB_YML))
          .forEach(propertySource -> environment.getPropertySources().addLast(propertySource));
    } catch (IOException e) {
      log.error("Wasn't able to load default web properties from {} file", LIB_YML);
    }
  }
}
