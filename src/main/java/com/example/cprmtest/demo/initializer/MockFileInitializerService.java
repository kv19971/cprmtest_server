package com.example.cprmtest.demo.initializer;

import com.example.cprmtest.demo.model.dao.CustomerDao;
import com.example.cprmtest.demo.model.dao.OptionDetailsDao;
import com.example.cprmtest.demo.model.dao.PortfolioAssetDao;
import com.example.cprmtest.demo.model.dao.StockDao;
import com.example.cprmtest.demo.model.dto.enums.OptionType;
import com.example.cprmtest.demo.model.dto.enums.TradeType;
import com.example.cprmtest.demo.model.entities.Customer;
import com.example.cprmtest.demo.model.entities.OptionDetails;
import com.example.cprmtest.demo.model.entities.PortfolioAsset;
import com.example.cprmtest.demo.model.entities.Stock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Date;
import java.util.function.Consumer;

//load data from file to db then trigger rest of initialization
@Component
public class MockFileInitializerService extends InitializerService{
    @Autowired
    private StockDao stockDao;

    @Autowired
    private CustomerDao customerDao;

    @Autowired
    private PortfolioAssetDao portfolioAssetDao;

    @Autowired
    private OptionDetailsDao optionDetailsDao;

    private static final String SPLITTER = ",";

    ClassLoader classloader;

    @Override
    public void initialize() {
        try {
            populateDB();
        } catch (IOException e) {
            e.printStackTrace();
        }

        super.initialize();

    }

    public void populateDB() throws IOException {
        classloader = Thread.currentThread().getContextClassLoader();

        fileLineToDb("mockdata/stocks.txt", (l) -> {
            try {
                lineToStock(l);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        fileLineToDb("mockdata/customers.txt", (l) -> {
            try {
                lineToCustomer(l);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        fileLineToDb("mockdata/assets.txt", (l) -> {
            try {
                lineToPortfolioAsset(l);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    private void fileLineToDb(String file, Consumer<String> insertionFunction) throws IOException {
        InputStream inputStream = classloader.getResourceAsStream(file);
        if(inputStream == null) {
            throw new IOException("IS null!");
        }
        InputStreamReader streamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader reader = new BufferedReader(streamReader);
        for (String line; (line = reader.readLine()) != null;) {
            insertionFunction.accept(line);
        }
    }

    private void lineToStock(String line) throws IOException {
        String[] splitted = line.split(SPLITTER);
        if(splitted.length != 3) {
            throw new IOException("Stock " + line + " given is invalid");
        }
        String ticker = splitted[0];
        Double sd = Double.parseDouble(splitted[1]);
        Double mean = Double.parseDouble(splitted[2]);
        Stock s = new Stock(ticker, sd, mean);
        stockDao.genericSaveEntity(s);
    }

    private void lineToCustomer(String line) throws IOException {
        String url = line.trim();
        Customer c = new Customer(url);
        customerDao.genericSaveEntity(c);
    }

    private void lineToPortfolioAsset(String line) throws IOException {
        String[] splitted = line.split(SPLITTER);
        if(splitted.length != 4 && splitted.length != 7) {
            throw new IOException("Asset " + line + " given is invalid");
        }

        Customer c = customerDao.findByUrl(splitted[0]);
        Stock s = stockDao.findByTicker(splitted[1]);
        Long qty = Long.parseLong(splitted[2]);
        TradeType type = TradeType.valueOf(splitted[3]);
        PortfolioAsset asset = portfolioAssetDao.genericSaveEntity(new PortfolioAsset(c, s, qty, type, null));
        if(splitted.length == 7) {
            OptionType optionType = OptionType.valueOf(splitted[4]);
            Double strikeprice = Double.valueOf(splitted[5]);
            Date expiryDate = Date.valueOf(splitted[6]);
            optionDetailsDao.genericSaveEntity(new OptionDetails(asset, optionType, strikeprice, expiryDate));
        }

    }
}
