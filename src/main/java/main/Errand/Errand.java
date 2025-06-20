package main.Errand;

import jakarta.persistence.*;
import main.ErrandData.ErrandData;
import org.antlr.v4.runtime.misc.NotNull;
import org.h2.util.json.JSONObject;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import main.Errand.DatePrefixedIdSequenceGenerator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table
public class Errand {
    @Id
//    @SequenceGenerator(
//            name = "errand_sequence",
//            sequenceName = "errand_sequence",
//            allocationSize = 1
//    )
    @GeneratedValue(
//            strategy = GenerationType.IDENTITY,
            strategy = GenerationType.SEQUENCE,
            generator = "errand_sequence"
    )
    @GenericGenerator(
            name = "errand_sequence",
            strategy = "main.Errand.DatePrefixedIdSequenceGenerator",
            parameters = {@Parameter(name = DatePrefixedIdSequenceGenerator.INCREMENT_PARAM, value = "1")}
    )
    @NotNull
    private String errandId;

    @NotNull
    private Long carId;

    @NotNull
    private Long drvId;
    private String plannedRoute;    //1-2-3-4... tak latwiej przechowywac w tablicy sql

    @OneToOne(mappedBy = "errand", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private ErrandData errandData;

    public Errand(String errandId, Long carId, Long drvId, String plannedRoute, ErrandData errandData) {
        this.errandId = errandId;
        this.carId = carId;
        this.drvId = drvId;
        this.plannedRoute = plannedRoute;
        this.errandData = errandData;
    }

    public Errand() {
    }

    public String getErrandId() {
        return errandId;
    }

    public void setErrandId(String errandId) {
        this.errandId = errandId;
    }

    public Long getCarId() {
        return carId;
    }

    public void setCarId(Long carId) {
        this.carId = carId;
    }

    public Long getDrvId() {
        return drvId;
    }

    public void setDrvId(Long drvId) {
        this.drvId = drvId;
    }

    public List<Long> getPlannedRouteAsList() {
        if (this.plannedRoute != null) {
            List<String> routeStringList = new ArrayList<String>(Arrays.asList(this.plannedRoute.split("-")));  //convert String to List<String>
            List<Long> routeList = routeStringList.stream().map(s -> Long.parseLong(s)).collect(Collectors.toList());   //convert List<String> to List<Long>
            return routeList;
        }
        else{
            return new ArrayList<Long>();
        }
    }

    public String getPlannedRoute(){
        return this.plannedRoute;
    }

    public void setPlannedRouteAsList(List<Long> plannedRoute) {
        String newRoute = String.join("-", (CharSequence) plannedRoute);
        this.plannedRoute = newRoute;
    }

    public void setPlannedRoute(String plannedRoute){
        this.plannedRoute = plannedRoute;
    }
}
