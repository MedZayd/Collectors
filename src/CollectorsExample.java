import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.gson.Gson;

public class CollectorsExample {

  public static void main(String[] args) {
    long startTime = System.nanoTime();
    List<Metric> metrics = getDataSet();
    System.out.println("DataSet Extraction: "+((System.nanoTime() - startTime)/1000000)+"ms");
    System.out.println("DataSet size: "+metrics.size());
      
    // Grouping by ParentRegionId, RegionId, CountryCode, FamilyId
    Map<Long, Map<Long, Map<String, Map<Long, Map<Long, List<Metric>>>>>> result =
      metrics.stream().collect(Collectors.groupingBy(
          Metric::getParentRegionId,
          Collectors.groupingBy(
              Metric::getRegionId, 
              Collectors.groupingBy(
                  Metric::getCountryCode,
                  Collectors.groupingBy(
                      Metric::getCustomerId,
                      Collectors.groupingBy(
                          Metric::getFamilyId
                      )
                  )
              )
          )
      ));
    System.out.println("Grouping with collectors: "+((System.nanoTime() - startTime)/1000000)+"ms");
    
    List<ParentRegion> parentRegions = new ArrayList<>();
    result.entrySet().forEach(pRegionEntry -> {
      // key: parent region
      ParentRegion pRegion = ParentRegion.builder().id(pRegionEntry.getKey()).build();
      List<Region> regions = new ArrayList<>();
      pRegionEntry.getValue().entrySet().forEach(regionEntry -> {
        // key: region
        Region region = Region.builder().id(regionEntry.getKey()).build();
        List<Country> countries = new ArrayList<>();
        regionEntry.getValue().entrySet().forEach(countryEntry -> {
          // key: country
          // Map<Long, List<Metric>> groupedByCountry = countryEntry.getValue().entrySet().stream().flatMap();
          Country country = Country.builder().id(countryEntry.getKey()).build();
          List<Customer> customers = new ArrayList<>();
          countryEntry.getValue().entrySet().forEach(customerEntry -> {
            // key: customer
            Customer customer = Customer.builder().id(customerEntry.getKey()).build();
            List<Family> families = new ArrayList<>();
            customerEntry.getValue().entrySet().forEach(familyEntry -> {
              // key: family
              families.add(
                  Family
                  .builder()
                  .id(familyEntry.getKey())
                  .volume(familyEntry.getValue().stream().collect(Collectors.summingDouble(Metric::getVolume)))
                  .build()
              );
            });
            customer.setFamilies(families);
            customers.add(customer);
          });
          country.setCustomers(customers);
          countries.add(country);
        });
        region.setCountries(countries);
        regions.add(region);
      });
      pRegion.setRegions(regions);
      parentRegions.add(pRegion);
    });
    System.out.println("Creating ParentRegion List: "+((System.nanoTime() - startTime)/1000000)+"ms");
    
    writeToFile(new Gson().toJson(parentRegions));
    System.out.println("Writing output to file: "+((System.nanoTime() - startTime)/1000000)+"ms");
  }

  private static List<Metric> getDataSet() {
    System.out.println("Getting Data Set from file");
    try {
      List<Metric> metrics = new ArrayList<>();
      File file = new File("src/dataset.xml");
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      DocumentBuilder db = dbf.newDocumentBuilder();
      Document doc = db.parse(file);
      doc.getDocumentElement().normalize();
      NodeList nodeList = doc.getElementsByTagName("record");
      for (int itr = 0; itr < nodeList.getLength(); itr++) {
        Node node = nodeList.item(itr);
        if (node.getNodeType() == Node.ELEMENT_NODE) {
          Element element = (Element) node;
          metrics.add(
              Metric
              .builder()
              .parentRegionId(Long.valueOf(element.getElementsByTagName("parent_region_id").item(0).getTextContent()))
              .regionId(Long.valueOf(element.getElementsByTagName("region_id").item(0).getTextContent()))
              .countryCode(element.getElementsByTagName("country_code").item(0).getTextContent())
              .customerId(Long.valueOf(element.getElementsByTagName("customer_id").item(0).getTextContent()))
              .familyId(Long.valueOf(element.getElementsByTagName("family_id").item(0).getTextContent()))
              .volume(Double.valueOf(element.getElementsByTagName("volume").item(0).getTextContent()))
              .build()
          );
        }
      }
      return metrics;
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    return new ArrayList<>();
  }
  
  private static void writeToFile(String json) {
    try {
      FileOutputStream fos = new FileOutputStream("output.json");
      ObjectOutputStream oos = new ObjectOutputStream(fos);   
      oos.writeObject(json); // write MenuArray to ObjectOutputStream
      oos.close(); 
    } catch(Exception ex) {
        ex.printStackTrace();
    }
  }
}
