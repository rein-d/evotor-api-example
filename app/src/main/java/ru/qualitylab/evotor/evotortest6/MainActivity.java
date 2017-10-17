package ru.qualitylab.evotor.evotortest6;

import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import ru.evotor.devices.commons.ConnectionWrapper;
import ru.evotor.devices.commons.DeviceServiceConnector;
import ru.evotor.devices.commons.exception.DeviceServiceException;
import ru.evotor.devices.commons.printer.PrinterDocument;
import ru.evotor.devices.commons.printer.printable.PrintableBarcode;
import ru.evotor.devices.commons.printer.printable.PrintableImage;
import ru.evotor.devices.commons.printer.printable.PrintableText;
import ru.evotor.devices.commons.services.IPrinterServiceWrapper;
import ru.evotor.devices.commons.services.IScalesServiceWrapper;
import ru.evotor.framework.core.IntegrationAppCompatActivity;
import ru.evotor.framework.core.IntegrationException;
import ru.evotor.framework.core.IntegrationManagerCallback;
import ru.evotor.framework.core.IntegrationManagerFuture;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenPaybackReceiptCommand;
import ru.evotor.framework.core.action.command.open_receipt_command.OpenSellReceiptCommand;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintReceiptCommandResult;
import ru.evotor.framework.core.action.command.print_receipt_command.PrintSellReceiptCommand;
import ru.evotor.framework.core.action.event.receipt.changes.position.PositionAdd;
import ru.evotor.framework.core.action.event.receipt.changes.position.SetExtra;
import ru.evotor.framework.navigation.NavigationApi;
import ru.evotor.framework.payment.PaymentSystem;
import ru.evotor.framework.payment.PaymentType;
import ru.evotor.framework.receipt.ExtraKey;
import ru.evotor.framework.receipt.Payment;
import ru.evotor.framework.receipt.Position;
import ru.evotor.framework.receipt.PrintGroup;
import ru.evotor.framework.receipt.Receipt;

public class MainActivity extends IntegrationAppCompatActivity {

    private Bitmap getBitmapFromAsset(String strName) {
        AssetManager assetManager = getAssets();
        InputStream istr = null;
        try {
            istr = assetManager.open(strName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeStream(istr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getApplicationContext().getPackageName());

        Button openReceipt = (Button) findViewById(R.id.btnOpenReceipt);
        Button openPayback = (Button) findViewById(R.id.btnOpenPayback);
        Button btnPrefs = (Button) findViewById(R.id.btnPrefs);
        Button btnPrefsC = (Button) findViewById(R.id.btnPrefsC);
        Button btnUserApi = (Button) findViewById(R.id.btnUserApi);
        Button btnPayApi = (Button) findViewById(R.id.btnPayAPI);
        Button btnReceiptApi = (Button) findViewById(R.id.btnReceiptAPI);
        Button btnOpenAndEmail = (Button) findViewById(R.id.btnOpenAndEmail);
        Button btnPrint = (Button) findViewById(R.id.btnPrint);
        Button btnInventoryApi = (Button) findViewById(R.id.btnInventoryApi);

        DeviceServiceConnector.startInitConnections(getApplicationContext());
        DeviceServiceConnector.addConnectionWrapper(new ConnectionWrapper() {
            @Override
            public void onPrinterServiceConnected(IPrinterServiceWrapper printerService) {
                Log.e("", "onPrinterServiceConnected");
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            DeviceServiceConnector.getPrinterService().printDocument(
                                    ru.evotor.devices.commons.Constants.DEFAULT_DEVICE_INDEX,
                                    new PrinterDocument(
                                            new PrintableText("INIT OK")));
                        } catch (DeviceServiceException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
            }

            @Override
            public void onPrinterServiceDisconnected() {
                Log.e("", "onPrinterServiceDisconnected");
            }

            @Override
            public void onScalesServiceConnected(IScalesServiceWrapper scalesService) {
                Log.e("", "onScalesServiceConnected");
            }

            @Override
            public void onScalesServiceDisconnected() {
                Log.e("", "onScalesServiceDisconnected");
            }
        });

        btnInventoryApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, InventoryApiActivity.class));
            }
        });

        btnPrint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread() {
                    @Override
                    public void run() {
                        try {
                            DeviceServiceConnector.getPrinterService().printDocument(
                                    ru.evotor.devices.commons.Constants.DEFAULT_DEVICE_INDEX,
                                    new PrinterDocument(
                                            new PrintableText("Первая строка"),
                                            new PrintableText("Довольно длинный текст, помещающийся лишь на несколько строк"),
                                            new PrintableBarcode("4606272036264", PrintableBarcode.BarcodeType.EAN13),
                                            new PrintableImage(getBitmapFromAsset("ic_launcher.png"))));
                        } catch (DeviceServiceException e) {
                            e.printStackTrace();
                        }

                    }
                }.start();
            }
        });

        btnOpenAndEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReceiptAndEmail();
            }
        });

        btnPrefs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(NavigationApi.createIntentForCashReceiptSettings());
            }
        });

        btnPrefsC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(NavigationApi.createIntentForCashRegisterReport());
            }
        });

        btnUserApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, UserApiActivity.class));
            }
        });

        btnReceiptApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, ReceiptApiActivity.class));
            }
        });

        btnPayApi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, PayApiActivity.class));
            }
        });

        openReceipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openReceipt();
            }
        });

        openPayback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openPayback();
            }
        });
    }

    public void openReceiptAndEmail() {
        List<Position> list = new ArrayList<>();
        list.add(
                Position.Builder.newInstance(
                        UUID.randomUUID().toString(),
                        null,
                        "1234",
                        "12",
                        0,
                        new BigDecimal(1000),
                        BigDecimal.TEN
                ).build()
        );
        list.add(
                Position.Builder.newInstance(
                        UUID.randomUUID().toString(),
                        null,
                        "1234",
                        "12",
                        0,
                        new BigDecimal(500),
                        BigDecimal.ONE
                ).setPriceWithDiscountPosition(new BigDecimal(300)).build()
        );
        HashMap payments = new HashMap<Payment, BigDecimal>();
        payments.put(new Payment(
                UUID.randomUUID().toString(),
                new BigDecimal(9300),
                new PaymentSystem(PaymentType.CASH, "Internet", "12424"),
                null,
                null,
                null
        ), new BigDecimal(9300));
        PrintGroup printGroup = new PrintGroup(UUID.randomUUID().toString(),
                PrintGroup.Type.CASH_RECEIPT, null, null, null, null, false);
        final Receipt.PrintReceipt printReceipt = new Receipt.PrintReceipt(
                null,
                list,
                payments,
                new HashMap<Payment, BigDecimal>()
        );

        ArrayList<Receipt.PrintReceipt> listDocs = new ArrayList<>();
        listDocs.add(printReceipt);
        BigDecimal receiptDiscount = new BigDecimal(1000);
        new PrintSellReceiptCommand(listDocs, null, "+79886023135", "admin@ncreaid.com", receiptDiscount).process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture integrationManagerFuture) {
                try {
                    IntegrationManagerFuture.Result result = integrationManagerFuture.getResult();
                    switch (result.getType()) {
                        case OK:
                            PrintReceiptCommandResult printSellReceiptResult = PrintReceiptCommandResult.create(result.getData());
                            Toast.makeText(MainActivity.this, "OK", Toast.LENGTH_LONG).show();
                            break;
                        case ERROR:
                            Toast.makeText(MainActivity.this, result.getError().getMessage(), Toast.LENGTH_LONG).show();
                            break;
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void openReceipt() {
        String prodName = "Зубочистки";
        String measureName = "kg";
        int measurePrecision = 0;
        BigDecimal price = new BigDecimal(200);
        BigDecimal quantity = new BigDecimal(1);

        List<PositionAdd> positionAddList = new ArrayList<>();
        Set<ExtraKey> set = new HashSet<>();
        set.add(new ExtraKey(null, null, "Тест Зубочистки"));
        positionAddList.add(new PositionAdd(Position.Builder.newInstance(
                UUID.randomUUID().toString(),
                null,
                prodName,
                measureName,
                measurePrecision,
                price,
                quantity
        ).setExtraKeys(set).build()));

        JSONObject object = new JSONObject();
        try {
            object.put("someSuperKey", "AWESOME RECEIPT OPEN");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SetExtra extra = new SetExtra(object);

        new OpenSellReceiptCommand(positionAddList, extra).process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture future) {
                try {
                    IntegrationManagerFuture.Result result = future.getResult();
                    if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
                        startActivity(new Intent("evotor.intent.action.payment.SELL"));
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void openPayback() {
        String prodName = "Зубочистки";
        String measureName = "kg";
        int measurePrecision = 0;
        BigDecimal price = new BigDecimal(200);
        BigDecimal quantity = new BigDecimal(1);

        List<PositionAdd> positionAddList = new ArrayList<>();
        Set<ExtraKey> set = new HashSet<>();
        set.add(new ExtraKey(null, null, "Тест Возврат Зубочистки"));
        positionAddList.add(new PositionAdd(Position.Builder.newInstance(
                UUID.randomUUID().toString(),
                null,
                prodName,
                measureName,
                measurePrecision,
                price,
                quantity
        ).setExtraKeys(set).build()));

        JSONObject object = new JSONObject();
        try {
            object.put("someSuperKey", "AWESOME PAYBACK OPEN");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        SetExtra extra = new SetExtra(object);

        new OpenPaybackReceiptCommand(positionAddList, extra).process(MainActivity.this, new IntegrationManagerCallback() {
            @Override
            public void run(IntegrationManagerFuture future) {
                try {
                    IntegrationManagerFuture.Result result = future.getResult();
                    if (result.getType() == IntegrationManagerFuture.Result.Type.OK) {
                        startActivity(new Intent("evotor.intent.action.payment.PAYBACK"));
                    }
                } catch (IntegrationException e) {
                    e.printStackTrace();
                }
            }
        });
    }


}
