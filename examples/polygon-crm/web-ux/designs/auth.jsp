<html>
<head>
    <title>Polygon ${title}</title>

    <meta name="viewport" content="width=device-width, initial-scale=1">

    <link rel="icon" type="image/png" href="/resources/media/icon.png">
    <link rel="stylesheet" href="/resources/css/default.css">

    <script type="text/javascript" src="/resources/js/packages/jquery.js"></script>
    <script type="text/javascript" src="/resources/js/packages/dygraphs.js"></script>

    <link rel="stylesheet" href="/resources/js/packages/dygraph.css">

    <style>
        html{margin-bottom:170px;}
        body{
            background:#c3d5d5;
            background:linear-gradient(90deg, #1d2222 0%, #fff 100%);
            background:#f4f8f8;
        }
        #header-identity{
            z-index: 0;
            height:317px;
            position: absolute;
            background: linear-gradient(65deg, rgba(57,120,227,1) 0%, rgba(56,119,225,1) 30%, rgba(126,217,251,1) 30%, rgba(126,217,251,1) 41%, rgba(254,245,122,1) 41%, rgba(254,245,122,1) 54%, rgba(240,231,113,1) 54%, rgba(246,241,173,1) 60%, rgba(255,255,255,1) 60%, rgba(255,255,255,1) 80%, rgba(255,129,122,1) 80%);
            /*background: rgb(2,0,36);*/
            /*background: linear-gradient(72deg, rgba(2,0,36,1) 0%, rgba(0,0,0,1) 12%, rgba(56,119,225,1) 12%, rgba(56,119,225,1) 43%, rgba(126,216,251,1) 43%, rgba(126,216,251,1) 67%, rgba(253,244,122,1) 67%, rgba(253,244,122,1) 82%, rgba(240,232,120,1) 82%, rgba(240,232,120,1) 89%, rgba(255,129,122,1) 89%);*/
            /*background: linear-gradient(72deg, rgba(56,119,225,1) 12%, rgba(56,119,225,1) 43%, rgba(126,216,251,1) 43%, rgba(126,216,251,1) 67%, rgba(240,232,120,1) 67%, rgba(240,232,120,1) 82%, rgba(253,244,122,1) 82%, rgba(253,244,122,1) 89%, rgba(255,129,122,1) 89%);*/
            background: linear-gradient(65deg, rgba(56,119,225,1) 12%, rgba(56,119,225,1) 43%, rgba(252,243,122,1) 43%, rgba(252,243,122,1) 67%, rgba(255,129,122,1) 67%, rgba(124,249,122,1) 67%, rgba(124,249,122,1) 89%, rgba(255,129,122,1) 89%);
            /*background: linear-gradient(65deg, rgba(56,119,225,1) 12%, rgba(56,119,225,1) 34%, rgba(228,219,101,1) 34%, rgba(228,219,101,1) 35%, rgba(252,243,122,1) 39%, rgba(252,243,122,1) 62%, rgba(95,210,93,1) 62%, rgba(95,210,93,1) 65%, rgba(102,220,100,1) 67%, rgba(124,249,122,1) 84%, rgba(231,106,100,1) 84%, rgba(255,129,122,1) 86%);*/
            background:linear-gradient(65deg, rgb(56, 119, 225) 12%, rgba(56,119,225,1) 34%, rgba(228,219,101,1) 34%, rgba(228,219,101,1) 35%, rgba(252,243,122,1) 39%, rgba(252,243,122,1) 62%, rgb(22 106 20) 62%, rgb(55 154 52) 65%, rgb(64 171 62) 67%, rgba(124,249,122,1) 84%, rgba(231,106,100,1) 84%, rgba(255,129,122,1) 86%);
            background: #3877E1;
        }
        .container{
            position:relative;
            padding:0px;
            border-radius: 9px;
            padding-bottom:200px;
        }

        #side-identity{
            position: fixed; top:0px; left:0px; bottom:0px; width:7px;
            background:linear-gradient(180deg, rgba(56,119,225,1) 12%, rgba(56,119,225,1) 34%, rgba(228,219,101,1) 34%, rgba(228,219,101,1) 35%, rgba(252,243,122,1) 39%, rgba(252,243,122,1) 62%, rgb(22 106 20) 62%, rgb(55 154 52) 65%, rgb(64 171 62) 67%, rgba(124,249,122,1) 84%, rgba(231,106,100,1) 84%, rgba(255,129,122,1) 86%);
        }

    </style>
</head>
<body>

<div id="header-identity"></div>
<%--<div id="side-identity"></div>--%>

<div class="container">

    <style>
        #logo-wrapper{text-decoration: none;float: right;display:block;text-align: center;padding-right:7px;}
        #polygon-icon{height:40px; width:40px;margin-top:7px;}
        #tagline{font-size:10px;}

        #header-wrapper{border-bottom: dotted 1px #d0dde3;}
        #header-menu{padding:0px 0px;}
        #header-menu li{float:left;}
        #header-menu li a{display:block;text-decoration: none; font-size:14px; padding:34px 20px;background-color: #fff; border-right:dotted 1px #d0dde3;text-transform: uppercase;}
        #header-menu li a:hover, #header-menu li a.active{color:#fff;background-color: #000; border-right:dotted 1px #222227;}
        .sales-activities{background-color:#fff;border-bottom: dotted 1px #d0dde3;}
        #search-action{display:inline-block;margin-left:20px;}
        #welcome{margin-right:20px;}
    </style>

    <div id="header-wrapper">
        <div id="header-menu-wrapper">
            <ul id="header-menu">
                <li><a href="/snapshot" id="menu-href-first" class="${snapshotHref}">Snapshot</a></li>
                <li><a href="/prospects" class="${searchHref}">Search</a></li>
                <li><a href="/prospects/create" class="${createHref}">Create</a></li>
            </ul>
        </div>

        <a href="/prospects" id="logo-wrapper">
            <div class="logo-inner-wrapper">
                <svg version="1.2" baseProfile="tiny-ps" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 189 182" width="189" height="182" id="polygon-icon">
                    <style>
                        .s0 { fill: #4aadd3 }
                        .s1 { fill: #7dd8fb }
                        .s2 { fill: #e4da53 }
                        .s3 { fill: #fdf47a }
                        .s4 { fill: #1e5ec9 }
                        .s5 { fill: #3877e1 }
                        .s6 { fill: #e1534b }
                        .s7 { fill: #f77068 }
                        .s8 { fill: #0d9c57 }
                        .s9 { fill: #20b66d }
                    </style>
                    <g id="Folder 2 copy 2">
                        <path id="Shape 13 copy 10" class="s0" d="m126.97 129h-40.52l0-40.52l40.52 40.52z" />
                        <path id="Shape 13 copy 9" class="s1" d="m86.45 88.48h40.52v40.52l-40.52-40.52z" />
                        <path id="Shape 13 copy 13" class="s2" d="m126.97 89h-40.52l0-40.52l40.52 40.52z" />
                        <path id="Shape 13 copy 14" class="s3" d="m86.45 48.48h40.52v40.52l-40.52-40.52z" />
                        <path id="Shape 13 copy 17" class="s4" d="m66.97 49h-40.52l0-40.52l40.52 40.52z" />
                        <path id="Shape 13 copy 18" class="s5" d="m26.45 8.48h40.52v40.52l-40.52-40.52z" />
                        <path id="Shape 13 copy 15" class="s6" d="m167.97 109h-40.52l0-40.52l40.52 40.52z" />
                        <path id="Shape 13 copy 16" class="s7" d="m127.45 68.48h40.52v40.52l-40.52-40.52z" />
                        <path id="Shape 13 copy 11" class="s8" d="m86.97 169h-40.52l0-40.52l40.52 40.52z" />
                        <path id="Shape 13 copy 12" class="s9" d="m46.45 128.48h40.52v40.52l-40.52-40.52z" />
                    </g>
                </svg>
                <h1 class="logo" style="margin:0px; line-height:1.0em; font-size:19px;">Polygon</h1>
                <span class="lightf" id="tagline">Open Source CRM</span>
            </div>
        </a>

        <div id="welcome">Hi <a href="/users/edit/${userId}">${username}</a>!</div>

        <br class="clear"/>

    </div>

    <style>
        #welcome-identity{float:right;margin-right:30px;}
        .value{font-weight: 900;font-size:32px;}
        .stat-wrapper{width:20%;float:left; padding:10px 30px;}
        .stat-wrapper{width:20%;float:left; padding:10px 30px;}
    </style>


    <div class="geb-sig-left">
        <span class="uno color"></span>
        <span class="dos color"></span>
        <span class="tres color"></span>
        <span class="quatro color"></span>
        <br class="clear"/>
    </div>
    <div class="geb-sig-left">
        <span class="cinco color"></span>
        <span class="seies color"></span>
        <span class="siete color"></span>
        <br class="clear"/>
    </div>

    <kakai:if spec="${prospectActivities.size() > 0}">
        <div class="sales-activities">
            <kakai:iterate items="${prospectActivities}" var="prospectActivity">
                <a href="/prospects/${prospectActivity.prospectId}" class="sales-activity">
                    <span class="activity-date"><strong>${prospectActivity.prettyTime}</strong> : ${prospectActivity.name}</span>
                    <span class="activity-prospect">${prospectActivity.prospectName}</span>
                </a>
            </kakai:iterate>
            <br class="clear"/>
        </div>
    </kakai:if>

    <br class="clear"/>

    <kakai:content/>

    <br class="clear"/>

    <div class="button-wrapper inside-container" style="margin-bottom:20px;">
        <a href="/users" class="href-dotted-black ${usersHref}">Users</a>
    </div>

    <br class="clear"/>
    <div class="button-wrapper-lonely" style="text-align: center; margin-bottom:120px;">2022 &copy; Polygon</div>

</div>

<br class="clear"/>

<script>
    $(document).ready(function(){
        let $activities = $('.sales-activities');
        let upwards = true;
        let interval = setInterval(function(){
            if(upwards) {
                $activities.scrollTop($activities.height());
            }
            if(!upwards) {
                $activities.scrollTop(1);
            }
            upwards = !upwards
        }, 6100);


        $.ajax({
            url : "/data",
            success: function(data){
                console.log(data);
                g = new Dygraph(
                    document.getElementById("graph"),
                    data,
                    {
                        legend: 'always',
                        titleHeight: 32,
                        ylabel: '# of Sales',
                        xlabel: 'Date',
                        strokeWidth: 5.0,
                    }
                );
            }
        })
    })
</script>

</body>
</html>