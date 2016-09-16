package nablarch.common.databind.csv;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import nablarch.common.databind.ObjectMapper;
import nablarch.common.databind.ObjectMapperFactory;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class PerformanceTest {


    @Test
    public void writeTest() throws IOException {
        for (int i = 0; i < 5; i++) {
            final long start = System.nanoTime();
            write(i);
            final long end = System.nanoTime();
            System.out.println("処理時間[" + i + "]:"  + TimeUnit.NANOSECONDS.toMillis(end - start));
        }
    }

    private void write(int count) throws IOException {
        final File file = File.createTempFile("out", String.valueOf(count));
        final FileOutputStream stream = new FileOutputStream(file);

        final ObjectMapper<Person> mapper = ObjectMapperFactory.create(Person.class, stream);
        for (int i = 0; i < 1000000; i++) {
            mapper.write(new Person(
                    "あいうえお",
                            "かきくけこ",
                            "住所ですあああああああああああああああああああああああ",
                            i,
                            "会社名_",
                            "会社の住所いいいいいいいいいいいいいいいいいい")
            );
        }
        mapper.close();
    }

    @Csv(
            type = Csv.CsvType.EXCEL,
            properties = {"firstName", "lastName", "address", "age", "company", "companyAddress"}
    )
    public static class Person {

        private String firstName;
        private String lastName;
        private String address;
        private Integer age;
        private String company;
        private String companyAddress;

        public Person() {
        }

        public Person(String firstName, String lastName, String address, Integer age, String company,
                String companyAddress) {
            this.firstName = firstName;
            this.lastName = lastName;
            this.address = address;
            this.age = age;
            this.company = company;
            this.companyAddress = companyAddress;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(String lastName) {
            this.lastName = lastName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public String getCompany() {
            return company;
        }

        public void setCompany(String company) {
            this.company = company;
        }

        public String getCompanyAddress() {
            return companyAddress;
        }

        public void setCompanyAddress(String companyAddress) {
            this.companyAddress = companyAddress;
        }
    }
}
