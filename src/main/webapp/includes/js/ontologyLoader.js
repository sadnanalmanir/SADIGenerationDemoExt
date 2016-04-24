/**
 * Created by sadnana on 25/02/16.
 */

var LoadDomainOnt = "loaddomainont";
var LoadServiceOnt = "loadserviceont";
var Viewsourcecode = "viewsourcecode";
var SADIServiceclass = "sadiserviceclass";

var ServiceParameters = "serviceparameters";
var Dbconnectionclass = "dbconnectionclass";
var Webxmlconf = "webxmlconf";
var Indexjsp = "indexjsp";
var Pomxml = "pomxml";

var RegisterServicePath = "registerservice";
var Registry = "registry";
var Register = "register";
var Services = "services";
var RemoveServices = "removeall";
var NumberOfRegisteredServices = "numsofervices";

var SPARQLQueryPath = 'queryengine';
var SharePath = 'sparqlonshare';


var loadedDomainOntology = "";
var loadedServiceOntology = "";
var loadedServiceClassCode = "";
var loadedDBConnClassCode = "";
var loadedwebxmlconf = "";
var loadedindexjsp = "";
var loadedpomxml = "";

var serviceName = "";
var serviceClass = "";
var serviceInputURI = "";
var serviceOutputURI = "";
var serviceDescription = "";
var serviceEmail = "";





var Slash = "/";
var encode = encodeURIComponent;
var slash = function (s) {
    return Slash + s;
};
var append = function (s1, s2) {
    return s1 + s2;
};
var WS = "ws";
var service = function (service) {
    return append(WS, slash(service));
};
// return the domain ontology URI from the textbox
var domainOntURI = function () {
    return $("#domainOntologyURI").val();
};
// return the service ontology URI from the textbox
var serviceOntURI = function () {
    return $("#serviceOntologyURI").val();
};
var loadDomainOntology = function (domOntURI, handler) {
    $.ajax({
        type: "POST",
        url: service(LoadDomainOnt),
        data: JSON.stringify(loadRequest(domOntURI)),
        contentType: "application/json; charset=utf-8",
        success: function (loadedOntologyContent) {
            handler(loadedOntologyContent);
        }
    });
};

var loadServiceOntology = function (servOntURI, handler) {
    $.ajax({
        type: "POST",
        url: service(LoadServiceOnt),
        data: JSON.stringify(loadRequest(servOntURI)),
        contentType: "application/json; charset=utf-8",
        success: function (loadedOntologyContent) {
            handler(loadedOntologyContent);
        }
    });
};

/*
var registerServiceAlternative = function (handler) {
    $.ajax({
        type: "POST",
        url: service(Registry + slash(Register)),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            handler(data);
        }
    });
};
*/
var displayServiceAlternative = function (handler) {
    $.ajax({
        type: "POST",
        url: service(Registry + slash(Services)),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            handler(data);
        }
    });
};

var getNumberOfRegisteredServices = function (handler) {
    $.ajax({
        type: "POST",
        url: service(Registry + slash(NumberOfRegisteredServices)),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            handler(data);
        }
    });
};

var removeAllRegisteredServices = function (handler) {
    $.ajax({
        type: "POST",
        url: service(Registry + slash(RemoveServices)),
        contentType: "application/json; charset=utf-8",
        success: function (data) {
            handler(data);
        }
    });
};


/*
var registerServiceURI = function (completeRegistryServiceURI, handler) {
    $("#loadingRegisterGIF").css("display", "block");
    $.ajax({
        type: "POST",
        url: service(RegisterServicePath),
        data: JSON.stringify(loadRequest(completeRegistryServiceURI)),
        contentType: "application/json; charset=utf-8",
        success: function (successMessage) {
            console.log('Message received : ' + successMessage);
            handler(successMessage);
            $('#registrationSuccessMsg').css("display", "block");
            setTimeout(function() {
                $('#registrationSuccessMsg').fadeOut('fast');
            }, 1000); // <-- time in milliseconds
            $("#loadingRegisterGIF").css("display", "none");
            //$('#registrationSuccessMsg').css("display", "none");
        }
    });
};
*/
var registerServiceURI = function (completeRegistryServiceURI, handler) {
    $("#loadingRegisterGIF").css("display", "block");
    $.ajax({
        type: "POST",
        //url: service(RegisterServicePath),
        url: service(Registry + slash(Register)),
        data: JSON.stringify(loadRequest(completeRegistryServiceURI)),
        contentType: "application/json; charset=utf-8",
        success: function (successMessage) {
            console.log('Message received : ' + successMessage);
            handler(successMessage);
            $('#registrationSuccessMsg').css("display", "block");
            setTimeout(function () {
                $('#registrationSuccessMsg').fadeOut('fast');
            }, 1000); // <-- time in milliseconds
            $("#loadingRegisterGIF").css("display", "none");
            //$('#registrationSuccessMsg').css("display", "none");

            // now display the services
            displayServiceAlternative(function (serviceDescriptionURIs) {
                console.log("alternative registering services " + serviceDescriptionURIs);
                //$('#registeredServicesWindowID').append(serviceDescriptionURIs);
                $('#registeredServicesWindowID').html("");
                $('#registeredServicesWindowID').append(serviceDescriptionURIs);

            });


        }
    });
};



/*
var registerServiceURI = function (liveServiceURI, handler) {
    $.ajax({
        type: "GET",
        url: 'http://cbakerlab.unbsj.ca:8080/sadi-registry-0.1.0-valet-sadi/register/',
        data: "serviceURI=" + liveServiceURI,
        datatype: 'jsonp',
        //contentType: "application/jsonp; charset=utf-8",
        success: function (data) {
            console.log('successfully registered' + data);
            handler();
        },
        error: function () {
            console.log('no data is asked for');
        }
    });
};
*/


var sendServiceParameters = function (serviceName, serviceClass, serviceInputURI, serviceOutputURI, serviceDescription, serviceEmail, handler) {
    // start the loading animation
    $("#loadingCodeGIF").css("display", "block");
    $.ajax({
        type: "POST",
        url: service(Viewsourcecode + slash(ServiceParameters)),
        //beforeSend: function() { $body.addClass("loading"); },
        //complete: function() { $body.removeClass("loading"); }
        data: JSON.stringify(sendParameters(serviceName, serviceClass, serviceInputURI, serviceOutputURI, serviceDescription, serviceEmail)),
        contentType: "application/json; charset=utf-8",
        success: function (codeCreationConfirmation) {

            //alert('Parameters sent successfully.');
            handler(codeCreationConfirmation);
            // stop the loading animation once handler returns the confirmation that code is generated
            $("#loadingCodeGIF").css("display", "none");
        }
    });
};

var loadServiceClassCode = function (handler) {
    $.ajax({
        type: "GET",
        url: service(Viewsourcecode + slash(SADIServiceclass)),
        contentType: "application/json; charset=utf-8",
        success: function (loadedServiceClassCode) {
            handler(loadedServiceClassCode);
        }
    });
};

var loadDBConnClassCode = function (handler) {
    $.ajax({
        type: "GET",
        url: service(Viewsourcecode + slash(Dbconnectionclass)),
        contentType: "application/json; charset=utf-8",
        success: function (loadedDBConnClassCode) {
            handler(loadedDBConnClassCode);
        }
    });
};

var loadWebXMLConfCode = function (handler) {
    $.ajax({
        type: "GET",
        url: service(Viewsourcecode + slash(Webxmlconf)),
        contentType: "application/json; charset=utf-8",
        success: function (loadedwebxmlconf) {
            handler(loadedwebxmlconf);
        }
    });
};

var loadIndexJSPCode = function (handler) {
    $.ajax({
        type: "GET",
        url: service(Viewsourcecode + slash(Indexjsp)),
        contentType: "application/json; charset=utf-8",
        success: function (loadedindexjsp) {
            handler(loadedindexjsp);
        }
    });
};


var loadPomXMLConf = function (handler) {
    $.ajax({
        type: "GET",
        url: service(Viewsourcecode + slash(Pomxml)),
        contentType: "application/json; charset=utf-8",
        success: function (loadedpomxml) {
            handler(loadedpomxml);
        }
    });
};

var executeSPARQLQueryonSHARE = function (sparqlQuery, handler) {
    $.ajax({
        type: "POST",
        url: service(SPARQLQueryPath + slash(SharePath)),
        data: JSON.stringify(loadSPARQLQueryRequest(sparqlQuery)),
        contentType: "application/json; charset=utf-8",
        success: function (resultsOfSPARQLonSHARE) {
            handler(resultsOfSPARQLonSHARE);
        }
    });
};

var loadSPARQLQueryRequest = function (sparqlQuery) {
    var sparqlSHAREQuery = (sparqlQuery) ? sparqlQuery : "";
    return {
        //iri should be matched
        query: sparqlSHAREQuery
    };
};


var loadRequest = function (ontURI) {
    var onturi = (ontURI) ? ontURI : "";
    return {
        //iri should be matched
        iri: onturi
    };
};

var sendParameters = function (serviceName, serviceClass, serviceInputURI, serviceOutputURI, serviceDescription, serviceEmail) {

    var sName = (serviceName) ? serviceName : "";
    var sClass = (serviceClass) ? serviceClass : "";
    var sInputURI = (serviceInputURI) ? serviceInputURI : "";
    var sOutputURI = (serviceOutputURI) ? serviceOutputURI : "";
    var sDescription = (serviceDescription) ? serviceDescription : "";
    var sEmail = (serviceEmail) ? serviceEmail : "";
    return {
        //all parameters should be matched in the Modeling in Java class
        serviceName: sName,
        serviceClass: sClass,
        serviceInputURI: sInputURI,
        serviceOutputURI: sOutputURI,
        serviceDescription: sDescription,
        serviceEmail: sEmail
    };
};


/*
 *     Start when document is loaded
 */

$(document).ready(function () {

    //var iframe = document.getElementById("mirrorRegistryiframeID");
    //iframe.src = iframe.src;



    var mappingRulesLoadClicked = false;
    var domainOntLoadClicked = false;
    var serviceOntLoadClicked = false;
    var loadSourceCodeBtnClicked = false;
    var registerServiceBtnClicked = false;
    // holds the name of the service selected from the dropdown menu
    var serviceSelectedName;
    var registeredServiceCounter = 0;

    /** Show sample sparql query on the services when loaded */
    $("#textareaSPARQLQueryID").val(spqrqlQuery1);

    var SPARQLqueryContent = '';

    /*
     * Generate Service Code tab.
     * Hide the button to display and the all text areas for the generated code
     */
    // hide the display code button
    $('#displaySourceCodeBtn').hide();
    // hide the button for opening registry
    $('#removeServicesBtn').hide();

    // hide the click to download botton
    //$('#registerServiceBtn').hide();
    // hide the heading for the tabs
    $("#sourceCodeDisplayTabsHeadingID").hide();
    // hide all tabs for displaying source code
    $("#sourceCodeDisplayTabsID").hide();
    // hide all the text areas for holding the code
    $("#serviceClassCodeTextareaID").hide();
    $("#mysqlConnCodeTextareaID").hide();
    $("#webXMLConfTextareaID").hide();
    $("#indexJSPCodeTextareaID").hide();
    $("#pomXMLConfTextareaID").hide();





    /*
     *  Valet SADI Tab
     */

    $('#rootwizard').bootstrapWizard({

        'nextSelector': '.button-next',
        'previousSelector': '.button-previous'
    });

    /*
     *  Step 7
     */
    $("#go2DemoBtn").click(function () {

        $('#tabs li:eq(1) a').tab('show');
    });

    /*
     *  Relational Databases Tab
     */

    // Magnify the TOHDW image 1.6 times the original size when hovering over
    $('#TOWDASchemaImageID').hover(function () {
        $("#TOWDASchemaImageID").addClass('transition');

    }, function () {
        $("#TOWDASchemaImageID").removeClass('transition');
    });
    // Go to the next tab
    $("#go2OntlolgiesTabBtn").click(function () {

        $('#tabs li:eq(2) a').tab('show');
    });

    /*
     *  Ontologies Tab
     */

    // Set the URI of the ontologies READ-ONLY
    $("#domainOntologyURI").prop("readonly", true);
    $("#serviceOntologyURI").prop("readonly", true);

    // load and display the domain ontology when clicked
    $('#loadDomOntBtn').click(function () {
        loadDomainOntology(encode(domainOntURI()), function (result) {
            domainOntLoadClicked = true;
            loadedDomainOntology = result;
            $("#domainOntologyContent").val(loadedDomainOntology);
            $("#domainOntologyContent").show();
            //$("#tabs")
            //$("#tabs").tabs('select', 1);
            //alert('sucks');
            //$('#myTab a:last').tab('show');
            // enable the second tab (index starting at 0)
            $('#tabs li:eq(2) a').tab('show');

        });
    });

    // load and display the service ontology when clicked
    $('#loadServOntBtn').click(function () {
        serviceOntLoadClicked = true;
        loadServiceOntology(encode(serviceOntURI()), function (result) {
            loadedServiceOntology = result;
            $("#serviceOntologyContent").val(loadedServiceOntology);
            $("#serviceOntologyContent").show();
            //$("#tabs")
            //$("#tabs").tabs('select', 1);
            //alert('sucks');
            //$('#myTab a:last').tab('show');
            // enable the second tab (index starting at 0)
            $('#tabs li:eq(2) a').tab('show');

        });
    });
    // Go to the next tab
    $("#go2mappingRulesTabBtn").click(function () {

        //if(domainOntLoadClicked ===  true && serviceOntLoadClicked === true)
        $('#tabs li:eq(3) a').tab('show');
        //else
        //  alert('Both ontologies must be loaded before generating SADI service code.')
    });

    /*
     *  Mapping Rules Tab
     */

    // Set the rules textarea read-only
    $("#PSOAMappingRulesContentID").prop("readonly", true);
    // Load the mapping rules when clicked
    $("#loadPSOAMappingRulesBtn").click(function () {
        $("#PSOAMappingRulesContentID").val(psoaRuleMLMapping);
        mappingRulesLoadClicked = true;
        // enable the fourth tab (index starting at 0)
        $('#tabs li:eq(3) a').tab('show');
    });
    // Go to the next tab
    $("#go2SourceCodeBtn").click(function () {
        //if(mappingRulesLoadClicked === true)
        $('#tabs li:eq(4) a').tab('show');
        //else
        //  alert('Mapping rules must be loaded before generating SADI service code.')
    });




    /*
     * Generate source code tab
     */

    // Select a service from the drop-down menu
    $("#serviceDropDownID").on("click", "li a", function () {
        // get the selected service name
        serviceSelectedName = $(this).text();

        // set parameters for the first service when selected, set READ-ONLY
        if (serviceSelectedName === 'getDiagnosisIDByPatientID') {

            loadSourceCodeBtnClicked = true;

            $('#ServiceNameID').val(serviceSelectedName);
            $('#ServiceNameID').prop("readonly", true);
            $('#ServiceClassID').val('ca.unbsj.cbakerlab.' + 'haiku-services.' + serviceSelectedName);
            $('#ServiceClassID').prop("readonly", true);
            $('#InputClassID').val('http://cbakerlab.unbsj.ca:8080/haitohdemo/haitoh-sadi-service-ontology.owl#' + serviceSelectedName + '_Input');
            $('#InputClassID').prop("readonly", true);
            $('#OutputClassID').val('http://cbakerlab.unbsj.ca:8080/haitohdemo/haitoh-sadi-service-ontology.owl#' + serviceSelectedName + '_Output');
            $('#OutputClassID').prop("readonly", true);
            $('#DescriptionID').val('Gets patient\'s diagnosis id based on the patient id');
            $('#DescriptionID').prop("readonly", true);
            $('#EmailID').val('sadnanalmanir@gmail.com');
            $('#EmailID').prop("readonly", true);
        }

        // set parameters for the second service when selected, set READ-ONLY
        if (serviceSelectedName === 'getDiagnosisCodeByDiagnosisID') {

            loadSourceCodeBtnClicked = true;

            $('#ServiceNameID').val(serviceSelectedName);
            $('#ServiceNameID').prop("readonly", true);
            $('#ServiceClassID').val('ca.unbsj.cbakerlab.' + 'haiku-services.' + serviceSelectedName);
            $('#ServiceClassID').prop("readonly", true);
            $('#InputClassID').val('http://cbakerlab.unbsj.ca:8080/haitohdemo/haitoh-sadi-service-ontology.owl#' + serviceSelectedName + '_Input');
            $('#InputClassID').prop("readonly", true);
            $('#OutputClassID').val('http://cbakerlab.unbsj.ca:8080/haitohdemo/haitoh-sadi-service-ontology.owl#' + serviceSelectedName + '_Output');
            $('#OutputClassID').prop("readonly", true);
            $('#DescriptionID').val('Gets patient\'s diagnosis code based on the diagnosis id');
            $('#DescriptionID').prop("readonly", true);
            $('#EmailID').val('sadnanalmanir@gmail.com');
            $('#EmailID').prop("readonly", true);
        }
    });

    /*
     * Generate source code for the selected service
     */
    //  generate the source code
    // event e is used later to make sure that the handler stays on the same tab and prevents default active tab
    $('#generateSourceCodeBtn').click(function (e) {
        e.preventDefault();
        // clear any code which is being displayed now
        //$("#serviceClassCodeTextareaID").val('');
        //$("#mysqlConnCodeTextareaID").val('');
        //$("#webXMLConfTextareaID").val('');
        //$("#indexJSPCodeTextareaID").val('');
        //$("#pomXMLConfTextareaID").val('');

        // Hide them whenever the Generate Code button is clicked
        // hide the button
        $('#displaySourceCodeBtn').hide();
        //$('#go2ServiceRegistryBtn').hide();
        //$('#registerServiceBtn').hide();
        // hide the heading for the tabs
        $("#sourceCodeDisplayTabsHeadingID").hide();
        // hide all tabs for displaying source code
        $("#sourceCodeDisplayTabsID").hide();
        // hide all the text areas for holding the code
        $("#serviceClassCodeTextareaID").hide();
        $("#mysqlConnCodeTextareaID").hide();
        $("#webXMLConfTextareaID").hide();
        $("#indexJSPCodeTextareaID").hide();
        $("#pomXMLConfTextareaID").hide();



        if (loadSourceCodeBtnClicked === true) {
            // Send the service parameters to initialize the code generator first, retrieved from the form
            serviceName = $('#ServiceNameID').val();
            serviceClass = $('#ServiceClassID').val();
            serviceInputURI = $('#InputClassID').val();
            serviceOutputURI = $('#OutputClassID').val();
            serviceDescription = $('#DescriptionID').val();
            serviceEmail = $('#EmailID').val();

            // call ajax for response
            sendServiceParameters(encode(serviceName),
                encode(serviceClass), encode(serviceInputURI),
                encode(serviceOutputURI), encode(serviceDescription),
                encode(serviceEmail),
                function (codeCreationConfirmation) {
                    // show the display code button if code generation is confirmed
                    if (!(codeCreationConfirmation === "")) {
                        console.log("MESSAGE RETURNED: " + codeCreationConfirmation);

                        // show the display code button
                        $('#displaySourceCodeBtn').show();
                        // show the click to download button
                        //$('#registerServiceBtn').show();
                    } else {
                        console.log("Possibly code generation was unsuccessful.")
                    }
                });

            $('#tabs li:eq(4) a').tab('show');
        } else {
            alert('Select a service from the menu.');
            // prevent the default behaviour of going to the first tab of the main tabs menu
            e.preventDefault();
            $('#tabs li:eq(4) a').tab('show');
        }

    });


    /*
     * Display source code in the designated textareas
     */
    $("#displaySourceCodeBtn").click(function (e) {

        e.preventDefault();
        // load and display the generated service class code
        loadServiceClassCode(
            function (result) {
                loadedServiceClassCode = result;
                /*
                 loadedServiceClassCode = '<p>' + '<pre>'+
                 ' <code class="language-java">' + result + '</pre>'+ ' </code>' + '</p>';

                 $("#serviceClassTab").append(loadedServiceClassCode);
                 */
                $("#serviceClassCodeTextareaID").val(loadedServiceClassCode);
                //$("#serviceClassTab").show();
            });
        // load and display the generated database connection class code
        loadDBConnClassCode(function (result) {
            loadedDBConnClassCode = result;
            /*
             loadedDBConnClassCode = '<p>' + '<pre>'+
             ' <code class="language-java">' + result + '</pre>'+ ' </code>' + '</p>';
             $('#mysqlConnectionTab').append(loadedDBConnClassCode);
             */
            $("#mysqlConnCodeTextareaID").val(loadedDBConnClassCode);
            //$("#serviceClassTab").show();
            //$('#tabs li:eq(1) a').tab('show');

        });

        // load and display the generated web.xml configuration code
        loadWebXMLConfCode(function (result) {
            loadedwebxmlconf = result;
            /*
             loadedwebxmlconf = '<p>' + '<pre>'+
             ' <code class="language-xml">' + result + '</pre>'+ ' </code>' + '</p>';
             $('#webXMLTab').append(loadedwebxmlconf);
             */
            $("#webXMLConfTextareaID").val(loadedwebxmlconf);
            //$("#serviceClassTab").show();
            //$('#tabs li:eq(1) a').tab('show');

        });

        // load and display the generated index.jsp code
        loadIndexJSPCode(function (result) {
            loadedindexjsp = result;
            /*
             loadedindexjsp = '<p>' + '<pre>'+
             ' <code class="language-jsp">' + result + '</pre>'+ ' </code>' + '</p>';
             $('#indexJSPTab').append(loadedindexjsp);
             */
            $("#indexJSPCodeTextareaID").val(loadedindexjsp);
            //$("#serviceClassTab").show();
            //$('#tabs li:eq(1) a').tab('show');

        });

        // load and display the generated pom.xml code
        loadPomXMLConf(function (result) {
            loadedpomxml = result;
            /*
             loadedpomxml = '<p>' + '<pre>'+
             ' <code class="language-java">' + loadedpomxml + '</pre>'+ ' </code>' + '</p>';
             $('#pomXMLTab').append(loadedpomxml);
             */
            $("#pomXMLConfTextareaID").val(loadedpomxml);
            //$("#serviceClassTab").show();
            //$('#tabs li:eq(1) a').tab('show');

        });

        // display the heading for the tabs
        $("#sourceCodeDisplayTabsHeadingID").show();
        // display all tabs for displaying source code
        $("#sourceCodeDisplayTabsID").show();
        // set the first (index 0) tab containing service class source code as ACTIVE

        $('a[href=#serviceClassTab]').tab('show');
        //$("#sourceCodeDisplayTabsID").tabs({ active: 0 });go2ServiceRegistryBtn

        // display all the text areas for holding the code
        $("#serviceClassCodeTextareaID").show();
        $("#mysqlConnCodeTextareaID").show();
        $("#webXMLConfTextareaID").show();
        $("#indexJSPCodeTextareaID").show();
        $("#pomXMLConfTextareaID").show();

        // hide the button
        $('#displaySourceCodeBtn').hide();


        //$('#tabs li:eq(4) a').tab('show');
    });

    /*
     *  Register services tab
     *  Register the services
     */
    // Open the registry window on button click


    // Select a service from the drop-down menu to register
    $("#serviceToRegisterDropDownID").on("click", "li a", function () {
        // get the selected serivce name
        var deployedServiceSelected = $(this).text();

        // set parameters for the first service when selected, set READ-ONLY
        if (deployedServiceSelected === 'getDiagnosisIDByPatientID') {

            registerServiceBtnClicked = true;
            console.log(deployedServiceSelected);

            $('#deployedServiceNameTxtFieldID').val('http://cbakerlab.unbsj.ca:8080/getDiagnosisIDByPatientID_demo/getDiagnosisIDByPatientID');
            $('#deployedServiceNameTxtFieldID').prop("readonly", true);

        }
        if (deployedServiceSelected === 'getDiagnosisCodeByDiagnosisID') {

            registerServiceBtnClicked = true;
            console.log(deployedServiceSelected);
            $('#deployedServiceNameTxtFieldID').val('http://cbakerlab.unbsj.ca:8080/getDiagnosisCodeByDiagnosisID_demo/getDiagnosisCodeByDiagnosisID');
            $('#deployedServiceNameTxtFieldID').prop("readonly", true);
            //registerServiceURI(encode(deployedServiceSelected), function () {});
        }
    });

    $('#registerServiceBtn').click(function (e) {
        e.preventDefault();
        var selectedServiceTxtFieldURI = $('#deployedServiceNameTxtFieldID').val();
        // clear any code which is being displayed now
        //$("#deployedServiceNameTxtFieldID").val('');

        if (registerServiceBtnClicked === true) {
            $('#removeServicesBtn').hide();

            // Send the service parameters to initialize the code generator first, retrieved from the form

            // call ajax for response, send both the registry and the service uri

            // We are going to use our own registry in /tmp/sadiregistry/ directory
            //var registryURI = 'http://cbakerlab.unbsj.ca:8080/sadi-registry-0.1.0-valet-sadi/register/?serviceURI=';
            var serviceURI = selectedServiceTxtFieldURI;
            //registerServiceURI(encode(registryURI+serviceURI), function (registrationConfirmation) {
            registerServiceURI(encode(serviceURI), function (registrationConfirmation) {
                if (!(registrationConfirmation === "")) {
                    console.log("MESSAGE RETURNED: " + registrationConfirmation);

                    // show the display code button
                    //$('#displaySourceCodeBtn').show();
                    $('#removeServicesBtn').show();



                    //iframe.src = iframe.src;
                    // show the click to download button
                    //$('#registerServiceBtn').show();
                } else {
                    console.log("Possibly service registration was unsuccessful.")
                }

            });
            /*
             sendServiceParameters(encode(serviceName),
             encode(serviceClass), encode(serviceInputURI),
             encode(serviceOutputURI), encode(serviceDescription),
             encode(serviceEmail),
             function (codeCreationConfirmation) {
             // show the display code button if code generation is confirmed
             if (!(codeCreationConfirmation === "")) {
             console.log("MESSAGE RETURNED: " + codeCreationConfirmation);

             // show the display code button
             $('#displaySourceCodeBtn').show();
             // show the click to download button
             //$('#registerServiceBtn').show();
             } else {
             console.log("Possibly code generation was unsuccessful.")
             }
             });*/
        } else {
            alert('Select a service from the menu.');
            // prevent the default behaviour of going to the first tab of the main tabs menu
            e.preventDefault();
            //$('#tabs li:eq(4) a').tab('show');
        }

    });

    /**
     * Check if there is any services to removing all registered services.
     */




    $('#removeServicesBtn').click(function (e) {

        e.preventDefault();
        /**
         *  Return the number of registered services.Hide the remove button if no services are found registered.
         */
        getNumberOfRegisteredServices(function (numOfRegisteredServices) {
            if ((numOfRegisteredServices >= 0)) {
                console.log("Number of registered services : " + numOfRegisteredServices);
                registeredServiceCounter = numOfRegisteredServices;
                //$('#removeServicesBtn').show();
                removeAllRegisteredServices(function (removalSuccessMsg) {
                    console.log("Response of Service Removal from the registry : " + removalSuccessMsg)
                    $('#registeredServicesWindowID').html("");
                    $('#deployedServiceNameTxtFieldID').val('')
                    registerServiceBtnClicked = false;
                    $('#removeServicesBtn').hide();
                });

            } else {
                alert("Error: No services found to be removed from the registry.");
            }

        });


    });



    // load and display the service ontology when clicked
    /*
    $('#registerBtn').click(function () {
        serviceOntLoadClicked = true;
        registerServiceAlternative(function (result) {
            console.log("alternative registering services " + result);
            //loadedServiceOntology = result;
            //$("#serviceOntologyContent").val(loadedServiceOntology);
            //$("#serviceOntologyContent").show();
            //$("#tabs")
            //$("#tabs").tabs('select', 1);
            //alert('sucks');
            //$('#myTab a:last').tab('show');
            // enable the second tab (index starting at 0)
            //$('#tabs li:eq(2) a').tab('show');

        });
        displayServiceAlternative(function (result) {
            console.log("alternative registering services " + result);

        });



    });*/

    /*
     *  Query services using SHARE QUery engines tab
     */
    // Clear the input text area
    $("#resetQueryBtnID").click(function () {
        $("#textareaSPARQLQueryID").val("");
    });
    // Load sample SPARQL Query
    $("#SampleQueryBtnID").click(function () {
        $("#textareaSPARQLQueryID").val(spqrqlQuery1);
    });
    // Execute SPARQL query
    $('#submitQueryBtnID').click(function (e) {
        e.preventDefault();

        SPARQLqueryContent = document.getElementById("textareaSPARQLQueryID").value;
        console.log('SPARQL Query : ' + SPARQLqueryContent);

        if(SPARQLqueryContent.length === 0){
            alert('No query to run, load sample query.');
        }
        else{
            executeSPARQLQueryonSHARE(encode(SPARQLqueryContent), function (result) {
                //loadedServiceOntology = result;
                console.log('Response from SHARE: '+ result)
                $('#resultsSPARQLWindowID').html("");
                $('#resultsSPARQLWindowID').html("<br/><h2>Results</h2></br>");
                $('#resultsSPARQLWindowID').append(result);
                //$("#serviceOntologyContent").val(loadedServiceOntology);
                //$("#serviceOntologyContent").show();
                //$("#tabs")
                //$("#tabs").tabs('select', 1);
                //alert('sucks');
                //$('#myTab a:last').tab('show');
                // enable the second tab (index starting at 0)
                //$('#tabs li:eq(2) a').tab('show');

            });



        }

    });


});