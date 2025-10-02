package io.zerows.epoch.component.transformer;

interface TransformerMessage {

    String INFO_ROTATE = "Zero container will select new Mode ( mode = {0} ).";

    String INFO_VTC = "( Verticle ) The deployment options has been captured: " +
        "instances = {0}, ha = {1}, content = \n{2}";

    String INFO_DELIVERY = "( Delivery ) The delivery options has been captured: " +
        "content = {0}";
}
