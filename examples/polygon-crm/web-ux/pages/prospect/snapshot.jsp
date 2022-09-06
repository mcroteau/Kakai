<style>
    .value{font-size:72px;font-weight:900;display: block;}
    .value.large{font-size:102px;}
    .lightf{font-weight: 300}
</style>

<div style="padding:0px 30px;" class="inside-container">
    <h1 style="margin:20px 0px 0px;">Snapshot</h1>
    <p>Quick overview of sales operations.</p>

    <div id="graph" style="width:100%; height:300px;"></div>

    <h3 class="center-align" id="conversion-rate">
        <span class="value large">${conversionRate}%</span>
        ${salesCount}/${completedCount}  <br/><span class="lightf">sales/total activities</span><br/>
        Conversion Rate
    </h3>

    <style>

        #conversion-rate{float: left;}

        #moveable{
            float:right;
            width:40%;
        }
        .stat{width:40%;text-align: center;margin:30px 7px;display: inline-block;}
        .value{font-size:19px;border:solid 3px #e4e8e9;border-radius: 100px;}
        .value.large{border:none;}
    </style>

    <div id="moveable">


        <span class="stat left-float" >
            <span class="value">${activeCount}</span>
            <span class="lightf">Incomplete<br/> Activities</span>
        </span>

        <span class="stat left-float">
            <span class="value">${completedCount}</span>
            <span class="lightf">Completed<br/>Activities</span>
        </span>
        <br class="clear"/>
    </div>
</div>